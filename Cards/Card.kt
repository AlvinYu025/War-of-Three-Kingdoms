package Cards

import Generals.General

abstract class Card(open var initializer: General?, open var receiver: General?) { //Basic, Command, Weapon, Horse
    //Four Suits: Heart, Spade, Diamond, Club
    var suit: String? = null //suit and rank are assigned at factory?
    var rank: String? = null
    abstract val name: String

    var appearTimes = 0
}

abstract class BasicCard(initializer: General?, receiver: General?): Card(initializer, receiver) {

}

abstract class CommandCard(initializer: General?, receiver: General?): Card(initializer, receiver) {
    var isNegated = false
    abstract var type:String
    abstract fun execute()
}

abstract class WeaponCard(initializer: General?, receiver: General?): Card(initializer, receiver) {
    open var distance: Int = 0
    abstract fun execute(initializer: General, receiver: General?)
}

abstract class DefenseCard(initializer: General?, receiver: General?): Card(initializer, receiver){
    open var valid: Boolean = true

    abstract fun execute(initializer: General, receiver: General?): Boolean
}
abstract class HorseCard(initializer: General?, receiver: General?): Card(initializer, receiver) {
     abstract fun run()
}