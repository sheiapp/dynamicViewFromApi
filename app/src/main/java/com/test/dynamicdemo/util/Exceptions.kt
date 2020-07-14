package com.test.dynamicdemo.util

import java.io.IOException

class NoInternetException(message:String):IOException(message)

class ApiExceptions(message:String):IOException(message)

class ActivityNotFountException(message:String):IOException(message)

class KeychainException(message: String):IOException(message)

class IllegalStateExceptions(message: String):IOException(message)