package com.ptv.ibeacon.receiver.ibeacon

import java.util.UUID

object IBeaconParser {
    fun parseIBeaconData(advertisement: ByteArray): IBeaconData? {
        // Check if the advertisement data is long enough to contain iBeacon data
        if (advertisement.size != 23) {
            return null // Not the expected iBeacon size
        }

        // Extract UUID (bytes 2 to 17)
        val uuidBytes = advertisement.sliceArray(2..17)
        val uuid = byteArrayToUuid(uuidBytes)

        val majorBytes = advertisement.sliceArray(18..19)
        val major = byteArrayToInt(majorBytes)

        // Extract Minor (bytes 20 to 21)
        val minorBytes = advertisement.sliceArray(20..21)
        val minor = byteArrayToInt(minorBytes)

        val txPower = advertisement[22].toInt()

        return IBeaconData(uuid, major, minor, txPower)
    }

    private fun byteArrayToUuid(uuidBytes: ByteArray): String {
        // Ensure the byte array is of the correct length (16 bytes)
        if (uuidBytes.size != 16) {
            throw IllegalArgumentException("Invalid UUID byte array length")
        }

        // Convert the byte array to most and least significant bits
        val mostSignificantBits = uuidBytes.sliceArray(0..7).fold(0L) { acc, byte ->
            (acc shl 8) or (byte.toLong() and 0xFF)
        }

        val leastSignificantBits = uuidBytes.sliceArray(8..15).fold(0L) { acc, byte ->
            (acc shl 8) or (byte.toLong() and 0xFF)
        }

        val uuid = UUID(mostSignificantBits, leastSignificantBits)
        return uuid.toString()
    }

    private fun byteArrayToInt(bytes: ByteArray): Int {
        // Ensure the byte array is of the correct length (2 bytes)
        if (bytes.size != 2) {
            throw IllegalArgumentException("Invalid byte array length for integer")
        }

        return (bytes[0].toInt() shl 8) or (bytes[1].toInt() and 0xFF)
    }
}

data class IBeaconData(val uuid: String, val major: Int, val minor: Int, val txPower: Int)