import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap

class UtilsTests {

  @Test
  fun testConvertJavaMapToKotlinMutableMap() {
    val javaMap = ConcurrentHashMap<Int, Int>()
    javaMap[1] = 475
    val kotlinMap: MutableMap<Int, Int> = javaMap
    assertThat(kotlinMap[1], equalTo(475))
  }
}