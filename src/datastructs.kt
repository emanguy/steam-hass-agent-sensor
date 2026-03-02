import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SensorType {
  @SerialName("boolean")
  BOOLEAN
}

@Serializable
enum class UpdateState {
  @SerialName("idle")
  IDLE {
    override val stringState = "idle"
  },
  @SerialName("updating")
  UPDATING {
    override val stringState = "updating"
  },
  @SerialName("updated")
  UPDATED {
    override val stringState = "updated"
  };

  abstract val stringState: String
}

@Serializable
data class Sensor(
  @SerialName("sensor_name")
  val sensorName: String, 
  @SerialName("sensor_icon")
  val sensorIcon: String, 
  @SerialName("sensor_state")
  val sensorState: String,
  @SerialName("sensor_type")
  val sensorType: SensorType? = null,
)

@Serializable
data class SensorSet(
  val schedule: String, 
  val sensors: List<Sensor>,
)
