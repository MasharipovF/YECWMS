package com.example.yecwms.core

enum class ErrorCodeEnums(val code: Int, val message: String) {
    INCORRECT_CREDENTIALS(100000027, "Имя пользователя или пароль неправильны"),
    SESSION_TIMEOUT(301, "Текущая сессия исчерпана!")

}