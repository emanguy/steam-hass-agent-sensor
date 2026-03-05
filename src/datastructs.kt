import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SensorType {
  @SerialName("boolean")
  BOOLEAN
}

enum class UpdateState {
  IDLE {
    override val stringState = "idle"
  },
  UPDATING {
    override val stringState = "updating"
  },
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
