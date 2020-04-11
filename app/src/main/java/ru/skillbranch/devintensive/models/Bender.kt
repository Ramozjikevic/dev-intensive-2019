package ru.skillbranch.devintensive.models

class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    fun askQuestion() : String = when (question) {
                Question.NAME -> Question.NAME.question
                Question.PROFESSION -> Question.PROFESSION.question
                Question.MATERIAL -> Question.MATERIAL.question
                Question.BDAY -> Question.BDAY.question
                Question.SERIAL -> Question.SERIAL.question
                Question.IDLE -> Question.IDLE.question
    }

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        if (question.answers.isEmpty()) return question.question to status.color
        return if (question.answers.contains(answer)) {
            question = question.nextQuestion()
            "Отлично - ты справился\n${question.question}" to status.color
        } else {
            badAnswer(answer)
        }
    }

    private fun badAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        return if (question.validateValue(answer)) {
            val result = question.validateMessage() +
                    (if (question.validateMessage().isEmpty()) "" else "\n") +
                    question.question

            result to status.color
        } else {
            val result = "Это неправильный ответ" +
                    (if (status == Status.CRITICAL) ". Давай все по новой" else "") +
                    "\n${question.question}"
            status = status.nextStatus()
            result to status.color
        }
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (ordinal < values().lastIndex) {
                values()[ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>) {
        NAME("Как меня зовут?", listOf("Бендер", "Bender")) {
            override fun validateValue(value: String): Boolean {
                for (answer in answers) {
                    if (answer.toLowerCase() == value) return true
                }
                return false
            }

            override fun validateMessage(): String = "Имя должно начинаться с заглавной буквы"

            override fun nextQuestion(): Question = PROFESSION
        },
        PROFESSION("Назови мою профессию?", listOf("сгибальщик", "bender")) {
            override fun validateValue(value: String): Boolean = answers.contains(value.toLowerCase())

            override fun validateMessage(): String = "Профессия должна начинаться со строчной буквы"

            override fun nextQuestion(): Question = MATERIAL
        },
        MATERIAL("Из чего я сделан?", listOf("металл", "дерево", "metal", "iron", "wood")) {
            override fun validateValue(value: String): Boolean {
                for (c in value.toCharArray()) {
                    if (c.isDigit()) return true
                }
                return false
            }

            override fun validateMessage(): String = "Материал не должен содержать цифр"

            override fun nextQuestion(): Question = BDAY
        },
        BDAY("Когда меня создали?", listOf("2993")) {
            override fun validateValue(value: String): Boolean {
                for (c in value.toCharArray()) {
                    if (!c.isDigit()) return true
                }
                return false
            }

            override fun validateMessage(): String = "Год моего рождения должен содержать только цифры"

            override fun nextQuestion(): Question = SERIAL
        },
        SERIAL("Мой серийный номер?", listOf("2716057")) {
            override fun validateValue(value: String): Boolean {
                if (value.length != 7) return true
                for (c in value.toCharArray()) {
                    if (!c.isDigit()) return true
                }
                return false
            }

            override fun validateMessage(): String = "Серийный номер содержит только цифры, и их 7"

            override fun nextQuestion(): Question = IDLE
        },
        IDLE("На этом все, вопросов больше нет", listOf()) {
            override fun validateValue(value: String): Boolean = true
            override fun validateMessage(): String = ""
            override fun nextQuestion(): Question = IDLE
        };

        abstract fun nextQuestion(): Question

        abstract fun validateValue(value: String): Boolean

        abstract fun validateMessage(): String
    }

}