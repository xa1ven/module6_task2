package com.example.backend

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
// ── Tables ────────────────────────────────────────────────────────────────────

object Users : IntIdTable() {
    val username     = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
}

object Prizes : IntIdTable() {
    val awardYear  = varchar("award_year", 10)
    val category   = varchar("category", 100)
    val fullName   = varchar("full_name", 255).default("")
    val motivation = text("motivation")
    val detailLink = varchar("detail_link", 255).nullable()
}

object Laureates : IntIdTable() {
    val prizeId    = reference("prize_id", Prizes)
    val fullName   = varchar("full_name", 255)
    val portion    = varchar("portion", 20)
    val motivation = text("motivation")
    val portraitUrl = varchar("portrait_url", 500).nullable()
}

object UserFavorites : Table("user_favorites") {
    val userId  = reference("user_id", Users)
    val prizeId = reference("prize_id", Prizes)
    override val primaryKey = PrimaryKey(userId, prizeId)
}

// ── DAOs ──────────────────────────────────────────────────────────────────────

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    var username     by Users.username
    var passwordHash by Users.passwordHash
}

class Prize(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Prize>(Prizes)
    var awardYear  by Prizes.awardYear
    var category   by Prizes.category
    var fullName   by Prizes.fullName
    var motivation by Prizes.motivation
    var detailLink by Prizes.detailLink
    val laureates  by Laureate referrersOn Laureates.prizeId
}

class Laureate(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Laureate>(Laureates)
    var prize       by Prize referencedOn Laureates.prizeId
    var fullName    by Laureates.fullName
    var portion     by Laureates.portion
    var motivation  by Laureates.motivation
    var portraitUrl by Laureates.portraitUrl
}

// ── Factory ───────────────────────────────────────────────────────────────────

object DatabaseFactory {
    fun init() {
        val url      = System.getenv("DB_URL")      ?: "jdbc:postgresql://localhost:5432/nobel_db"
        val user     = System.getenv("DB_USER")     ?: "nobel_user"
        val password = System.getenv("DB_PASSWORD") ?: "nobel_pass"
        Database.connect(
            url      = url,
            driver   = "org.postgresql.Driver",
            user     = user,
            password = password
        )
        transaction {
            SchemaUtils.create(Users, Prizes, Laureates, UserFavorites)
            if (User.count() == 0L) seedData()
        }
    }

    private fun seedData() {
        User.new { username = "admin"; passwordHash = hashPassword("password") }
        User.new { username = "user1"; passwordHash = hashPassword("1234") }

        fun prize(year: String, cat: String, name: String, mot: String) = Prize.new {
            awardYear = year; category = cat; fullName = name; motivation = mot
        }
        fun laureate(p: Prize, name: String, por: String, mot: String) = Laureate.new {
            prize = p; fullName = name; portion = por; motivation = mot
        }

        val p1 = prize("2023", "Physics", "",
            "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter")
        laureate(p1, "Pierre Agostini", "1/3", "for experimental methods that generate attosecond pulses of light")
        laureate(p1, "Ferenc Krausz",   "1/3", "for experimental methods that generate attosecond pulses of light")
        laureate(p1, "Anne L'Huillier", "1/3", "for experimental methods that generate attosecond pulses of light")

        val p2 = prize("2023", "Chemistry", "", "for the discovery and synthesis of quantum dots")
        laureate(p2, "Moungi G. Bawendi", "1/3", "for the discovery and synthesis of quantum dots")
        laureate(p2, "Louis E. Brus",     "1/3", "for the discovery and synthesis of quantum dots")
        laureate(p2, "Alexei I. Ekimov",  "1/3", "for the discovery and synthesis of quantum dots")

        prize("2023", "Literature", "Jon Fosse",
            "for his innovative plays and prose which give voice to the unsayable")

        prize("2023", "Peace", "Narges Mohammadi",
            "for her fight against the oppression of women in Iran and her efforts to promote human rights")

        val p5 = prize("2022", "Physics", "",
            "for experiments with entangled photons, establishing the violation of Bell inequalities")
        laureate(p5, "Alain Aspect",    "1/3", "for experiments with entangled photons")
        laureate(p5, "John F. Clauser", "1/3", "for experiments with entangled photons")
        laureate(p5, "Anton Zeilinger", "1/3", "for experiments with entangled photons")

        val p6 = prize("2022", "Chemistry", "", "for click chemistry and bioorthogonal chemistry")
        laureate(p6, "Carolyn R. Bertozzi", "1/3", "for the development of bioorthogonal chemistry")
        laureate(p6, "Morten Meldal",       "1/3", "for the development of click chemistry")
        laureate(p6, "K. Barry Sharpless",  "1/3", "for the development of click chemistry")
    }
}
