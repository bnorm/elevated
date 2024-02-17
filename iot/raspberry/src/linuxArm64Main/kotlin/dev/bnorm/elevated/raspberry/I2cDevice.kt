package dev.bnorm.elevated.raspberry

import dev.bnorm.lib.i2c.I2C_SLAVE
import io.ktor.utils.io.errors.PosixException
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CValues
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import platform.posix.O_RDONLY
import platform.posix.O_WRONLY
import platform.posix.close
import platform.posix.ioctl
import platform.posix.open
import platform.posix.write

class I2cDevice(
    private val address: UInt = 98u,
    private val bus: UInt = 1u,
) {
    private val readFile: Int
    private val writeFile: Int

    init {
        readFile = open("/dev/i2c-$bus", O_RDONLY)
        writeFile = open("/dev/i2c-$bus", O_WRONLY)

        if (ioctl(readFile, I2C_SLAVE.toULong(), address) < 0) {
            error("Can not communicate with I2C device at specified address")
        }
        if (ioctl(writeFile, I2C_SLAVE.toULong(), address) < 0) {
            error("Can not communicate with I2C device at specified address")
        }
    }

    fun readBytes(count: Int): ByteArray {
        memScoped {
            val buffer = allocArray<ByteVar>(count)
            val bytes = read(buffer, count)
            return buffer.readBytes(bytes.toInt())

        }
    }

    private fun read(buffer: CArrayPointer<ByteVar>, count: Int): Long {
        val bytes = platform.posix.read(readFile, buffer, count.toULong())
        if (bytes < 0) throw PosixException.forErrno()
        return bytes
    }

    fun write(bytes: ByteArray) {
        memScoped {
            bytes.usePinned { pinned ->
                write(pinned.addressOf(0), bytes.size.toULong())
            }
        }
    }

    fun write(string: String) {
        write(string.cstr)
    }

    private fun write(buffer: CValuesRef<ByteVar>, count: ULong): Long {
        val bytes = write(writeFile, buffer, count)
        if (bytes < 0) throw PosixException.forErrno()
        return bytes
    }

    private fun write(cstr: CValues<ByteVar>): Long = write(cstr, cstr.size.convert())

    fun close() {
        close(readFile)
        close(writeFile)
    }
}
