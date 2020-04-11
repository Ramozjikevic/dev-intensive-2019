package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion(): String = when (question) {
        Question.NAME -> Question.NAME.question
        Question.PROFESSION -> Question.PROFESSION.question
        Question.MATERIAL -> Question.MATERIAL.question
        Question.BDAY -> Question.BDAY.question
        Question.SERIAL -> Question.SERIAL.question
        Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        if (question.answer.isEmpty()) return question.question to status.color

        return if (question.answer.contains(answer)) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${question.question}" to status.color
        } else {
            wrongAnswer(answer)
        }
    }

    fun wrongAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        return if (answer.isBlank() || question.validate(answer) != null) {
            "${question.notValidMessage}\n${question.question}" to status.color
        } else {
            status = status.nextStatus()
            if (status != Status.NORMAL) {
                "Это неправильный ответ\n${question.question}" to status.color
            } else {
                "Это неправильный ответ. Давай все по новой\n${Question.NAME.question}" to status.color
            }
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (this.ordinal < values().lastIndex) {
                values()[this.ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(
        val question: String,
        val answer: List<String>,
        val notValidMessage: String
    ) {
        NAME(
            "Как меня зовут?",
            listOf("Бендер", "Bender"),
            "Имя должно начинаться с заглавной буквы"
        ) {
            override fun nextQuestion(): Question = PROFESSION
            override fun validate(value: String): String? = if (!isUpperCase(value)) notValidMessage  else null
        },
        PROFESSION(
            "Назови мою профессию?",
            listOf("сгибальщик", "bender"),
            "Профессия должна начинаться со строчной буквы"
        ) {
            override fun nextQuestion(): Question = MATERIAL
            override fun validate(value: String): String? =
                if (isUpperCase(value)) notValidMessage else null
        },
        MATERIAL(
            "Из чего я сделан?",
            listOf("металл", "дерево", "metal", "iron", "wood"),
            "Материал не должен содержать цифр"
        ) {
            override fun nextQuestion(): Question = BDAY
            override fun validate(value: String) =
                if (isOnlyNumber(value)) notValidMessage else null
        },
        BDAY(
            "Когда меня создали?",
            listOf("2993"),
            "Год моего рождения должен содержать только цифры"
        ) {
            override fun nextQuestion(): Question = SERIAL
            override fun validate(value: String): String? =
                if (!isOnlyNumber(value)) notValidMessage else null
        },
        SERIAL(
            "Мой серийный номер?",
            listOf("2716057"),
            "Серийный номер содержит только цифры, и их 7"
        ) {
            override fun nextQuestion(): Question = IDLE
            override fun validate(value: String): String? =
                if (!isValidSerialNumber(value)) notValidMessage else null
        },
        IDLE("На этом все, вопросов больше нет", listOf(), "") {
            override fun nextQuestion(): Question = IDLE
            override fun validate(value: String): String? = null
        };

        abstract fun validate(value: String): String?
        abstract fun nextQuestion(): Question

        fun isUpperCase(value: String) = value.first().isUpperCase()
        fun isOnlyNumber(value: String) = value.matches(Regex("""\d+"""))
        fun isValidSerialNumber(value: String) = isOnlyNumber(value) && value.count() == 7
    }

}