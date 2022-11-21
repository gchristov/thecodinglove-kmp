package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.kmpcommonfirebase.CommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonfirebase.CommonNetworkModule
import com.gchristov.thecodinglove.kmpsearchdata.SearchDataModule
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

external fun require(module:String) : dynamic
external var exports: dynamic

fun main(args: Array<String>) {
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request, response ->
        val client = CommonNetworkModule.injectHttpClient()
        GlobalScope.launch {
            val userResponse: Response = client.get("https://reqres.in/api/users").body()

            println("About to test Firestore")
            val firestore = CommonFirebaseModule.injectFirestore()
            val document = firestore.document("preferences/user1").get()
            val count = document.get<Int>("count")
            println("Got Firestore document: exists=${document.exists}, count=$count")
            val batch = firestore.batch()
            batch.set(firestore.document("preferences/user1"), Count(count + 1))
            batch.commit()

            println("About to test html parser")
            val parser = SearchDataModule.injectPostParser()

            response.send(Messenger().message() + ", " + userResponse.page + ", " + count + parser.parse())
        }
    }
}

@Serializable
private data class Response(
    val page: Int
)

@Serializable
private data class Count(
    val count: Int
)