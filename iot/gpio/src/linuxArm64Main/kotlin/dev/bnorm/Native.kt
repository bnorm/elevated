/*
 * Copyright © 2021 Pavel Kakolin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@file:Suppress("UNCHECKED_CAST")

package dev.bnorm

import dev.bnorm.gpio.Gpio
import dev.bnorm.gpio.Gpiod
import dev.bnorm.i2c.I2c
import dev.bnorm.i2c.SMBus
import dev.bnorm.spi.Spi
import dev.bnorm.spi.SpiImpl
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import platform.posix.SEEK_END
import platform.posix.SEEK_SET
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell
import platform.posix.nanosleep
import platform.posix.size_t
import platform.posix.time_t
import platform.posix.timespec

fun UInt.toSizeT(): size_t = toULong()
fun Long.toSizeT(): size_t = toULong()
fun Int.toSizeT(): size_t = toULong()
fun <T> ULong.toIoctlRequest(): T = this as T

fun Long.toTimeT(): time_t = this

actual fun sleep(sec: Long, nanos: Long) {
    val spec = cValue<timespec> {
        tv_sec = sec.toTimeT()
        tv_nsec = nanos.toTimeT()
    }
    nanosleep(spec, null)
}

actual fun readFile(path: String): ByteArray {
    val f = fopen(path, "rb")
    try {
        fseek(f, 0, SEEK_END)
        val size = ftell(f)
        fseek(f, 0, SEEK_SET)

        return memScoped {
            val buff = allocArray<ByteVar>(size)
            fread(buff, 1u, size.toSizeT(), f)
            buff.readBytes(size.toInt())
        }
    } finally {
        fclose(f)
    }
}

actual fun Gpio(n: Int, consumer: String): Gpio = Gpiod(n, consumer)
actual fun Spi(device: String): Spi = SpiImpl(device)
actual fun I2c(n: Int): I2c = SMBus(n)
