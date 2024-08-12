package Cards

import GeneralManager.discardDeck
import Generals.General

class Zhuge_Crossbow(initializer: General?, receiver: General?) : WeaponCard(initializer, receiver) {
    override val name = "Zhuge Crossbow"
    override var distance = 1

    override fun execute(initializer: General, receiver: General?) {
        println("[Zhuge Crossbow] Continuous \"Attack\"s enabled.")
    }
}

class Yin_Yang_Swords(initializer: General?, receiver: General?) : WeaponCard(initializer, receiver) {
    override val name = "Yin-Yang Swords"
    override var distance = 2
    override fun execute(initializer: General, receiver: General?) {
        if (receiver != null) {
            if (!initializer.gender.equals(receiver.gender)) {
                val random = (0..1).random()
                if (random <= 0.5) {
                    println("[Yin-Yang Swords] ${initializer.name} chooses to let ${receiver.name} to discard one card.")
                    val before = receiver.hand.size
                    receiver.discard(1)
                    val after = receiver.hand.size
                    println("Now, ${initializer.name}'s hand size becomes from ${before} to ${after}.")
                } else {
                    println("[Yin-Yang Swords] ${initializer.name} chooses to let himself/herself to draw one card.")
                    val before = initializer.hand.size
                    initializer.draw(1)
                    val after = initializer.hand.size
                    println("Now, ${initializer.name}'s hand size becomes from ${before} to ${after}.")
                }
            } else {
                println("[Yin-Yang Swords] fails to take effect.")
            }
        }
    }
}

class Green_Dragon_Blade(initializer: General?, receiver: General?) : WeaponCard(initializer, receiver) {
    override val name = "Green Dragon Blade"
    override var distance = 3
    override fun execute(initializer: General, receiver: General?) {
        println("[Green Dragon Blade] \"Attack\" enabled again.")
    }
}

class Serpent_Spear(initializer: General?, receiver: General?) : WeaponCard(initializer, receiver) {
    override val name = "Serpent Spear"
    override var distance = 3
    override fun execute(initializer: General, receiver: General?) {
        if (initializer.hand.size >= 2) {
            initializer.discard(2)
            val list = mutableListOf("Heart", "Spade", "Diamond", "Club")
            if (receiver != null) {
                println("[Serpent Spear] ${initializer.name} spends an Attack to ${receiver.name} by discarding 2 cards.")
                receiver.beingAttacked(initializer, list.random())
            }
        }
        else {
            println("[Serpent Spear] ${initializer.name} cannot spend an Attack.")
        }
    }
}

class Rock_Cleaving_Axe(initializer: General?, receiver: General?) : WeaponCard(initializer, receiver) {
    override val name = "Rock Cleaving Axe"
    override var distance = 3
    override fun execute(initializer: General, receiver: General?) {
        if (initializer.hand.size >= 2) {
            initializer.discard(2)
            val list = mutableListOf("Heart", "Spade", "Diamond", "Club")
            println("[Rock Cleaving Axe] \"Attack\" is forced to take effect by ${initializer.name} discarding 2 cards.")
            receiver!!.beingAttacked_Rock_Cleaving_Axe(initializer, list.random())
        }
        else {
            println("[Rock Cleaving Axe] ${initializer.name} cannot spend an Attack.")
        }
    }
}

class Kirin_Bow(initializer: General?, receiver: General?) : WeaponCard(initializer, receiver) {
    override val name = "Kirin Bow"
    override var distance = 5
    override fun execute(initializer: General, receiver: General?) {
        if (receiver!!.equipment.size != 0) {
            val random = receiver.equipment.random()
            receiver.equipment.remove(random)
            discardDeck.add(random)
            println("[Kirin Bow] ${receiver.name}'s Horse Card: ${random.name} is abandoned.")
        } else {
            println("[Kirin Bow] ${receiver.name} does not have a horse.")
        }
    }
}

class Blue_Steel_Blade(initializer: General?, receiver: General?) : WeaponCard(initializer, receiver) {
    override val name = "Blue Steel Blade"
    override var distance = 2
    override fun execute(initializer: General, receiver: General?) {
        if (receiver!!.defense != null) {
            receiver.defense!!.valid = false
            println("[Blue Steel Blade] ${receiver.name}'s Defense Card: ${receiver.defense!!.name} is neglected.")
        }
        else {
            println("[Blue Steel Blade] ${receiver.name} does not have a defense card.")
        }
    }
}