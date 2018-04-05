package neobis.alier.poputchik.model

import java.io.Serializable
import java.util.*

/**
 * Created by Alier on 03.04.2018.
 */
data class Info(var id: Int? = -1,
                var name: String? = null,
                var phone: String? = null,
                var isDriver: Boolean = false,
                var start_address: String? = null,
                var end_address: String? = null,
                var start_latitude: Double? = null,
                var start_longitude: Double? = null,
                var start_time: String? = null,
                var description: String? = null,
                var available_places: Int? = 0) : Serializable