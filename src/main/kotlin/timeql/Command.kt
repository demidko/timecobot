package timeql

import kotlin.time.DurationUnit

class Command(type: Type, unit: DurationUnit, duration: Int) {
  enum class Type { CheckTime, TransferTime, BanUser, RedeemUser }
}