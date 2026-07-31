package com.example.screenmirror

import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.InputStream

class ScreenStreamServer(port: Int) : NanoHTTPD(port) {

    @Volatile
    private var frameData: ByteArray? = null

    fun onFrameCaptured(bytes: ByteArray) {
        this.frameData = bytes
    }

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri

        return if (uri == "/video") {
            // Emisión constante de fotogramas de la pantalla (MJPEG)
            newChunkedResponse(
                Response.Status.OK,
                "multipart/x-mixed-replace; boundary=--frame",
                getFrameStream()
            )
        } else {
            // Interfaz visual que recibe el televisor
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body { margin:0; background:#000; display:flex; justify-content:center; align-items:center; height:100vh; }
                        img { max-width:100%; max-height:100vh; object-fit:contain; }
                    </style>
                </head>
                <body>
                    <img src="/video" />
                </body>
                </html>
            """.trimIndent()
            newFixedLengthResponse(html)
        }
    }

    private fun getFrameStream(): InputStream {
        return ByteArrayInputStream(frameData ?: ByteArray(0))
    }
}
