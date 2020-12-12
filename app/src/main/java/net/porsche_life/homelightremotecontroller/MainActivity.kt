package net.porsche_life.homelightremotecontroller

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MainActivity : AppCompatActivity() {

    private lateinit var mqttAndroidClient: MqttAndroidClient

    private val clientId = System.currentTimeMillis()
    private val serverUri = BuildConfig.MQTT_HOST
    private val remoconLightTopic = "remocon/light"
    private val remoconAirTopic = "remocon/air"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mqttAndroidClient = MqttAndroidClient(applicationContext, BuildConfig.MQTT_HOST, clientId.toString())
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) {
                    addToHistory("Reconnected to : $serverURI")
//                    subscribeToTopic()
                } else {
                    addToHistory("Connected to: $serverURI")
                }
            }

            override fun connectionLost(cause: Throwable) {
                addToHistory("The Connection was lost.")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                addToHistory("Incoming message: " + String(message.payload))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                addToHistory("Delivery complete.")
            }
        })

        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.isCleanSession = true
        mqttConnectOptions.userName = BuildConfig.MQTT_USERNAME
        mqttConnectOptions.password = BuildConfig.MQTT_PASSWORD.toCharArray()

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    addToHistory("Success to connect to: $serverUri")
                    val disconnectedBufferOptions = DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions)

                    findViewById<Button>(R.id.buttonOn).setOnClickListener { view ->
                        publishToLightTopic("on")
                    }

                    findViewById<Button>(R.id.buttonOff).setOnClickListener { view ->
                        publishToLightTopic("off")
                    }

                    findViewById<Button>(R.id.buttonSmall).setOnClickListener { view ->
                        publishToLightTopic("small")
                    }

                    findViewById<Button>(R.id.buttonAirOff).setOnClickListener { view ->
                        publishToAirTopic("off")
                    }

                    findViewById<Button>(R.id.buttonCooler).setOnClickListener { view ->
                        publishToAirTopic("cooler")
                    }

                    findViewById<Button>(R.id.buttonHeater).setOnClickListener { view ->
                        publishToAirTopic("heater")
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    addToHistory("Failed to connect to: $serverUri")
                    exception.printStackTrace()
                }
            })

        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    private fun publishToLightTopic(status: String) {
        val messageStr = "{\"status\":\"" + status + "\"}"
        val message = MqttMessage(messageStr.toByteArray())
        message.qos = 0
        message.isRetained = false
        try {
            mqttAndroidClient.publish(remoconLightTopic, message)
        } catch (ex: MqttPersistenceException) {
            System.err.println("Exception whilst subscribing")
            ex.printStackTrace();

        }  catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing")
            ex.printStackTrace()
        }
    }

    private fun publishToAirTopic(status: String) {
        val messageStr = "{\"status\":\"" + status + "\"}"
        val message = MqttMessage(messageStr.toByteArray())
        message.qos = 0
        message.isRetained = false
        try {
            mqttAndroidClient.publish(remoconAirTopic, message)
        } catch (ex: MqttPersistenceException) {
            System.err.println("Exception whilst subscribing")
            ex.printStackTrace();

        }  catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing")
            ex.printStackTrace()
        }
    }

    private fun addToHistory(mainText: String) {
        Log.d("tag", "LOG: $mainText")
    }
}