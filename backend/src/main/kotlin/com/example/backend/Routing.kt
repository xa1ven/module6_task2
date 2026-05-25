package com.example.backend

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

// ── DTOs ──────────────────────────────────────────────────────────────────────

@Serializable data class LoginRequest(val username: String, val password: String)
@Serializable data class TokenResponse(val token: String)

@Serializable
data class LaureateDtoServer(
    val id: Int,
    val fullName: String,
    val portion: String,
    val motivation: String,
    val portraitUrl: String? = null
)

@Serializable
data class PrizeResponse(
    val id: Int,
    val awardYear: String,
    val category: String,
    val fullName: String,
    val motivation: String,
    val detailLink: String? = null,
    val laureates: List<LaureateDtoServer> = emptyList()
)

// Must be called inside a transaction
fun Prize.toResponse() = PrizeResponse(
    id         = id.value,
    awardYear  = awardYear,
    category   = category,
    fullName   = fullName,
    motivation = motivation,
    detailLink = detailLink,
    laureates  = laureates.map { l ->
        LaureateDtoServer(l.id.value, l.fullName, l.portion, l.motivation, l.portraitUrl)
    }.toList()
)

// ── Plugins ───────────────────────────────────────────────────────────────────

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

// ── Routes ────────────────────────────────────────────────────────────────────

fun Application.configureRouting() {
    routing {

        // POST /auth/login
        post("/auth/login") {
            val req  = call.receive<LoginRequest>()
            val hash = hashPassword(req.password)

            val user = transaction {
                User.find { Users.username eq req.username }.firstOrNull()
            }

            if (user == null || user.passwordHash != hash) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                return@post
            }

            val token = JWT.create()
                .withIssuer(JWT_ISSUER)
                .withAudience(JWT_AUDIENCE)
                .withClaim("username", user.username)
                .withClaim("userId", user.id.value)
                .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000L))
                .sign(Algorithm.HMAC256(JWT_SECRET))

            call.respond(TokenResponse(token))
        }

        // GET /prizes
        get("/prizes") {
            val prizes = transaction { Prize.all().map { it.toResponse() } }
            call.respond(prizes)
        }

        // GET /prizes/{id}
        get("/prizes/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val prize = transaction { Prize.findById(id)?.toResponse() }
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(prize)
        }

        authenticate("auth-jwt") {

            // GET /users/me/prizes
            get("/users/me/prizes") {
                val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                val prizes = transaction {
                    val user = User.findById(userId) ?: return@transaction emptyList()
                    UserFavorites
                        .select { UserFavorites.userId eq user.id }
                        .mapNotNull { row ->
                            Prize.findById(row[UserFavorites.prizeId].value)?.toResponse()
                        }
                }
                call.respond(prizes)
            }

            // POST /users/me/prizes/{id}
            post("/users/me/prizes/{id}") {
                val userId  = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                val prizeId = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest)

                transaction {
                    val user  = User.findById(userId)  ?: return@transaction
                    val prize = Prize.findById(prizeId) ?: return@transaction
                    val exists = UserFavorites
                        .select { (UserFavorites.userId eq user.id) and (UserFavorites.prizeId eq prize.id) }
                        .any()
                    if (!exists) {
                        UserFavorites.insert {
                            it[UserFavorites.userId]  = user.id
                            it[UserFavorites.prizeId] = prize.id
                        }
                    }
                }
                call.respond(HttpStatusCode.OK)
            }

            // DELETE /users/me/prizes/{id}
            delete("/users/me/prizes/{id}") {
                val userId  = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                val prizeId = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                transaction {
                    val user  = User.findById(userId)  ?: return@transaction
                    val prize = Prize.findById(prizeId) ?: return@transaction
                    UserFavorites.deleteWhere {
                        with(SqlExpressionBuilder) {
                            (UserFavorites.userId eq user.id) and (UserFavorites.prizeId eq prize.id)
                        }
                    }
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
