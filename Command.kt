import Generals.General

//interface Command {
//    open fun execute() {}
//
//}
// Command function type
typealias Command = () -> Unit
typealias CommandGenerator = (General) -> Command

//class Acedia(private val receiver: General) : Command {
//    override fun execute() {
//
//
//        val nums: MutableList<Int> = mutableListOf(1, 2, 3, 4)
//        val dice = nums.random()
//        if (dice != 1) {
//            receiver.isSkip = true
//            val name = receiver.name
//            println("$name can't dodge the Acedia card. Skipping one round of Play Phase.")
//        } else {
//            val name = receiver.name
//            println("$name successfully dodge the acedia card")
//        }
//    }
//
//
//}

//val Acedia: CommandGenerator = { receiver ->
//    {     val nums: MutableList<Int> = mutableListOf(1, 2, 3, 4)
//        val dice = nums.random()
//        if (dice != 1) {
//            receiver.isSkip = true
//            val name = receiver.name
//            println("$name can't dodge the Acedia card. Skipping one round of Play Phase.")
//        } else {
//            val name = receiver.name
//            println("$name successfully dodge the acedia card")
//        }}
//}