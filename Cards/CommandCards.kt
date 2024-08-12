package Cards

import GeneralManager.addDeck
import GeneralManager.deck
import GeneralManager.discardDeck
import GeneralManager.generals
import Generals.*

class Acedia(override var initializer: General?, override var receiver: General?) :
    CommandCard(initializer, receiver) { //In Acedia, this general is receiver. So, overriding is needed.
    override val name: String = "Acedia"
    override var type = "judgement"


    override fun execute() {

        if (isNegated == true) {
            println("The judgement card $name is negated")
            isNegated = false
            receiver!!.commands.remove(this)
            discardDeck.add(this)
            return
        }
        addDeck()
        val card = deck.removeAt(0)
        if (!card.suit?.uppercase().equals("HEART")) {
            receiver?.isSkip = true
            println("${receiver?.name} is lucky, no need to skip a turn")
        } else {
            receiver?.isSkip = false
            println("${receiver?.name} is unlucky,need to skip a turn")
        }
        receiver!!.commands.remove(this)
        discardDeck.add(card)
        discardDeck.add(this)
    }

}

class Barbarians(override var initializer: General?, override var receiver: General?) :
    CommandCard(initializer, receiver) {
    override val name: String = "Barbarians"
    override var type = "neutral"

    override fun execute() {
        var caoCao: General? = null

        if (isNegated == true) {
            println("${initializer?.name} try to launch a command Barbarian, but the command $name is negated")
            isNegated = false
            initializer!!.hand.remove(this)
            discardDeck.add(this)
            return
        }
        println("${initializer?.name} launch a command Barbarian, every one need to spend an attack card to avoid the attack")

        for (gen in generals) {


            var isDodged = false


            if (gen.hasAttackCard() && !gen.equals(initializer)) {
                isDodged = true
                //spend one attack card to dodge the attack
                gen.spendAttackCard1()
                println("${gen.name} successfully dodge the Barbarian by spending an Attack card.")
            }
            if (gen.equals(initializer)) {
                isDodged = true
            }
            if (!gen.hasAttackCard() && !gen.equals(initializer) && gen.hand.size >= 2 && gen.weapon is Serpent_Spear) {
                (gen.weapon as Serpent_Spear).execute(gen, null)
                println("${gen.name} successfully dodge the Barbarian by spending an Attack card.")
                isDodged = true
            }
            if (isDodged == false) {
                if (gen is CaoCao) {
                    caoCao = gen
                }
                gen.currentHP--
                if (gen.currentHP >= 0)
                    println("${gen?.name} is hurt by Barbarians, current HP is ${gen?.currentHP}.")
                else
                    println("${gen?.name} is hurt by Barbarians, current HP is 0.")
                if (gen.currentHP <= 0) {
                    gen.alive = false
                    gen.killer = initializer
                    gen.currentHP = 0
                    println("${gen.name}'s hp is ${gen.currentHP} now!")
                }

            }

        }
        initializer!!.hand.remove(this)
        discardDeck.add(this)

        if (caoCao is CaoCao) {
            caoCao.treachery(this)
        }
    }

}

class Duel(override var initializer: General?, override var receiver: General?) : CommandCard(initializer, receiver) {
    override val name: String = "Duel"

    override var type = "bad"


    override fun execute() {
        var target = receiver


        if (receiver == initializer || !receiver?.alive!!) return



        if (initializer!!.lvbu == true) {
            target!!.lvbu = true
        }
        if (isNegated == true) {
            println("the command $name is negated")
            isNegated = false
            initializer!!.hand.remove(this)
            discardDeck.add(this)
            return
        }
        println("${initializer?.name} launch a duel with ${receiver?.name}")
        if (target?.hasAttackCard() == false) {
            if (initializer is XuChu) {
                target!!.currentHP -= 2
                if (target!!.currentHP >= 0) {
                    println("${target!!.name} lose the duel, current Hp is ${target!!.currentHP}")
                } else {
                    println("${target!!.name} lose the duel, current Hp is 0")
                }

                if (target!!.currentHP <= 0) {
                    target!!.alive = false
                    target!!.killer = initializer
                    target!!.currentHP = 0
                    println("${target!!.name} lose the duel, current HP is ${target!!.currentHP} now!")
                }
            } else {
                target!!.currentHP--
                if (target!!.currentHP >= 0)
                    println("${target!!.name} lose the duel, current Hp is ${target!!.currentHP}")
                else
                    println("${target!!.name} lose the duel, current Hp is 0")

                if (receiver!!.currentHP <= 0) {
                    receiver!!.alive = false
                    receiver!!.killer = initializer
                    receiver!!.currentHP = 0
                    println("${target!!.name}'s hp is ${target!!.currentHP} now!")
                }

            }

            initializer!!.hand.remove(this)
            discardDeck.add(this)
            if (target.currentHP > 0 && target is CaoCao) {
                target.treachery(this)
            }
            return
        } else if (target?.hasAttackCard() == true) {
            target!!.spendAttackCard()
            target!!.lvbu = false
//            if(target == initializer){
//                target = receiver
//            }
//            else{
            var t = initializer
            initializer = receiver
            receiver = t
            execute()

        }

    }

}


class RainArrows(override var initializer: General?, override var receiver: General?) :
    CommandCard(initializer, receiver) {
    override val name: String = "RainArrows"
    override var type = "neutral"

    override fun execute() {
        if (isNegated == true) {
            println("${initializer?.name} try to launch a command rainArrow, but the command $name is negated")
            isNegated = false
            initializer!!.hand.remove(this)
            discardDeck.add(this)
            return
        }
        println("${initializer?.name} launch a command rainArrow, every one need to spend a dodge card to avoid the attack")

        for (gen in generals) {
            if (gen is CaoCao && initializer !is CaoCao) {
                var helpCao = false
                if (gen.eight1 == true) {
                    println("$name's [Eight Trigrams Formation] to avoid a defect.")
                } else if (gen.nextGeneral != null) {
                    gen.nextGeneral!!.canHelpLord(initializer!!)
                    gen.canActivateEntourageList.forEach {
                        if (it) {
                            helpCao = true
                        }
                    }
                }
                if (!helpCao && !gen.eight1) {
                    gen.treachery(this)
                }
                gen.eight1 = false
            } else {
                var isDodged = false


                if (!gen.equals(initializer)&&gen.hasDodgeCard()) {
                    isDodged = true
                    //spend one attack card to dodge the attack
                    if (gen.eight1 == true) {
                        println("$name's [Eight Trigrams Formation] to avoid a defect.")
                        gen.eight1 = false
                    } else {
                        gen.spendDodgeCard()
                        println("${gen.name} successfully dodge the rainArrows by spending a Dodge card.")
                    }
                }
                if (gen.equals(initializer)) {
                    isDodged = true
                }
                if (isDodged == false) {
                    gen.currentHP--
                    if (gen.currentHP >= 0)
                        println("${gen?.name} is hurt by RainArrows, current HP is ${gen?.currentHP}.")
                    else
                        println("${gen?.name} is hurt by RainArrows, current HP is 0.")
                    if (gen.currentHP <= 0) {
                        gen.alive = false
                        gen.killer = initializer
                        gen.currentHP = 0
                        println("${gen.name}'s hp is ${gen.currentHP} now!")
                    }
                }
            }
        }
        initializer!!.hand.remove(this)
        discardDeck.add(this)
    }

}


class StealingSheep(override var initializer: General?, override var receiver: General?) :
    CommandCard(initializer, receiver) {
    override val name: String = "StealingSheep"

    override var type = "bad"
    var canWork = true
    fun check(): Boolean {
        if (checkDistance(
                initializer!!,
                receiver!!
            ) >= initializer!!.attackRange && receiver?.hand?.size!! > 0 && receiver?.equipment?.size!! > 0
        ) {
            canWork = false

        }
        return canWork
    }


    override fun execute() {
        check()
        if (canWork == false) {
            return
        }


        if (isNegated == true) {
            println("${initializer?.name} try to steal a card from ${receiver?.name}, but the command $name is negated")
            isNegated = false
            initializer!!.hand.remove(this)
            discardDeck.add(this)
            return
        }
        if (receiver?.hasEquipment() == true) {
            if (receiver is SunShangXiang == false) {
                var eq = receiver?.loseEquipment()
                if (eq != null) {
                    initializer?.hand?.add(eq)
                }
                println("${initializer?.name} steal a card ${eq?.name} from ${receiver?.name}")
            } else {
                val gen = generals as? SunShangXiang
                var eq = gen?.loseEquipment()
                if (eq != null) {
                    initializer?.hand?.add(eq)
                }
            }

            initializer!!.hand.remove(this)
            discardDeck.add(this)


        } else {
            var hasHandCard = false
            for (gen in generals) {
                if (gen.equals(initializer) == false && gen.hand.size > 0)
                    hasHandCard = true
            }
            if (!hasHandCard)
                return


            while (receiver?.hand?.size == 0 || receiver?.equals(initializer) == true) {
                receiver = generals.random()
            }
            val toSteal = receiver?.hand?.random()
            toSteal?.let {
                receiver?.hand?.remove(it)
                initializer?.hand?.add(it)
                print("${initializer?.name} steal a card ${toSteal.name} from ${receiver?.name}. Hand: ")
                initializer?.showHand()
            }
            initializer!!.hand.remove(this)
            discardDeck.add(this)

        }
    }

}

//Burning bridge.
class Dismantle(override var initializer: General?, override var receiver: General?) :
    CommandCard(initializer, receiver) {
    override val name: String = "Dismantle"
    var canWork = true
    override var type = "bad"
    fun check(): Boolean {
//        if(checkDistance(initializer!!, receiver!!) >= initializer!!.attackRange && receiver?.hand?.size!! >0 && receiver?.equipment?.size!! >0){
//            canWork = false
//        }
        return canWork
    }


    override fun execute() {
        if (check() == false)
            return

        if (isNegated == true) {
            println("${initializer?.name} try to dismantle a card from ${receiver?.name}, but the command $name is negated")
            isNegated = false
            initializer!!.hand.remove(this)
            discardDeck.add(this)

            return
        }
//        if(receiver?.hasEquipment()== true){
//            var count = 0
//
//            for (eq in receiver!!.equipment){
//                if(count<1) {
//                    receiver?.hand?.remove(eq)
//                    discardDeck.add(eq)
//                    count++
//                    println("${initializer?.name} dismantle a card ${eq.name} from ${receiver?.name}")
//                }
//            }
//        }
        if (receiver?.hasEquipment() == true) {
            if (receiver is SunShangXiang == false) {
                var eq = receiver!!.loseEquipment()
                if (eq != null) {
                    discardDeck.add(eq)
                }
                println("${initializer?.name} dismantle equipment ${eq?.name} from ${receiver?.name}")
            } else {
                val gen = receiver as? SunShangXiang

                var eq = gen?.loseEquipment()

                if (eq != null) {
                    discardDeck.add(eq)
                    println("${initializer?.name} dismantle equipment ${eq?.name} from ${receiver?.name}")

                }


            }
            initializer!!.hand.remove(this)
            discardDeck.add(this)

        } else {
            var hasHandCard = false
            for (gen in generals) {
                if (gen.equals(initializer) == false && gen.hand.size > 0)
                    hasHandCard = true
            }
            if (!hasHandCard)
                return

            while (receiver?.hand?.size == 0 || receiver?.equals(initializer) == true) {
                receiver = generals.random()
            }
            val toSteal = receiver?.hand?.random()
            toSteal?.let {
                receiver?.hand?.remove(it)
                discardDeck.add(it)

            }
            initializer!!.hand.remove(this)
            discardDeck.add(this)
            println("${initializer?.name} dismantle a card ${initializer!!.suitMap[toSteal!!.suit]} ${toSteal.rank} ${toSteal.name} from ${receiver?.name}")

        }


    }


}


class Negate(override var initializer: General?, override var receiver: General?) : CommandCard(initializer, receiver) {
    override val name: String = "Negate"
    var commandCard: CommandCard? = null
    override var type = "Negate"


    override fun execute() {
        if (commandCard?.isNegated == false)
            commandCard?.isNegated = true

        initializer!!.hand.remove(this)
        discardDeck.add(this)
    }

}
