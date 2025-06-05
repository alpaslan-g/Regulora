package com.example.regulora.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttService(context: Context) {
    private val client = MqttAndroidClient(
        context, "tcp://broker.hivemq.com:1883", MqttClient.generateClientId()
    )

    fun connect(onConnected: () -> Unit) {
        val opts = MqttConnectOptions().apply {
            isCleanSession = true
        }
        client.connect(opts, null, object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                Log.d("MQTT", "Connected")
                onConnected()
            }

            override fun onFailure(token: IMqttToken?, e: Throwable?) {
                Log.e("MQTT", "Failed", e)
            }
        })
    }

    fun publish(topic: String, message: String) {
        client.publish(topic, MqttMessage(message.toByteArray()))
    }
}
