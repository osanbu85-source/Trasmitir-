package com.example.screenmirror

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class DeviceScanner {

    interface OnDeviceFoundListener {
        fun onDeviceFound(name: String, ipAddress: String)
    }

    fun scanNetwork(listener: OnDeviceFoundListener) {
        Thread {
            try {
                // Petición SSDP universal para detectar Smart TVs
                val query = "M-SEARCH * HTTP/1.1\r\n" +
                        "HOST: 239.255.255.250:1900\r\n" +
                        "MAN: \"ssdp:discover\"\r\n" +
                        "MX: 3\r\n" +
                        "ST: ssdp:all\r\n\r\n"

                val socket = DatagramSocket()
                socket.soTimeout = 4000
                val group = InetAddress.getByName("239.255.255.250")
                val packet = DatagramPacket(query.toByteArray(), query.length, group, 1900)
                
                socket.send(packet)

                val buffer = ByteArray(2048)
                while (true) {
                    val responsePacket = DatagramPacket(buffer, buffer.size)
                    socket.receive(responsePacket)
                    val response = String(responsePacket.data, 0, responsePacket.length)
                    val ip = responsePacket.address.hostAddress ?: ""

                    if (response.contains("Samsung") || response.contains("LG") || response.contains("Roku") || response.contains("MediaRenderer")) {
                        listener.onDeviceFound("Smart TV ($ip)", ip)
                    }
                }
            } catch (e: Exception) {
                // Timeout normal al finalizar la búsqueda
            }
        }.start()
    }
}
