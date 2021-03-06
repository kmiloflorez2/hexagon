package co.there4.hexagon.rest

import co.there4.hexagon.store.IdRepositoryTest
import co.there4.hexagon.client.Client
import co.there4.hexagon.server.Server
import co.there4.hexagon.server.ServerEngine
import co.there4.hexagon.settings.SettingsManager.settings
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

abstract class CrudTest <T : Any, K : Any> (
    type: KClass<T>, key: KProperty1<T, K>, engine: ServerEngine) :
    IdRepositoryTest<T, K>(type, key) {

    val server = Server(engine, settings)
    val client by lazy { Client("http://${server.bindAddress.hostAddress}:${server.runtimePort}") }

    fun startServer() {
        server.crud(idCollection)
        server.run()
    }

    fun stopServer() {
        server.stop()
    }

    fun crud_operations_behave_properly() {
//        val objects = createObjects ()
//        val changedObjects = objects.map { this.changeObject(it) }
//        val ids = objects.map { idCollection.getKey(it) }
//
//        client.post("/")
    }
}
