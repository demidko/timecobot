import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class AppKtTest {

  @Test
  fun testLocalhostServer() {

    localhostServer().start(false)

    val status = runBlocking {
      HttpClient().use {
        it.get<HttpStatusCode>()
      }
    }

    assertThat(status, equalTo(OK))
  }
}