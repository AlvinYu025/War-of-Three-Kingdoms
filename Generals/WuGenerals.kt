package Generals

import Cards.*
import GeneralManager.addDeck
import GeneralManager.deck
import GeneralManager.discardDeck
import HuangGaiStrategy
import Lord
import Loyalist
import LoyalistStrategy
import LvMengStrategy
import Player
import Rebel
import RebelStrategy
import Spy
import Strategy
import SunQuanStrategy
import SunShangXiangStrategy

class SunQuan(player: Player) : WuGeneral(player) {
    override var maxHP = 5
    override var name: String = "Sun Quan"
    override var sun = true


    init {
        initializeIdentityStrategy()
    }

    fun initializeIdentityStrategy() {// For generals who have special strategy, this helps
        // implement their action that is affected by their
        //identity instead of their general skill
        strategy = SunQuanStrategy(this)
        var stra: Strategy? = null
        if (player is Rebel) {
            stra = RebelStrategy(this)
        } else if (player is Lord) {
            stra = LoyalistStrategy(this)
        } else if (player is Loyalist) {
            stra = LoyalistStrategy(this)
        } else if (player is Spy) {
            if ((player as? Spy)!!.isRevealed == false) {
                stra = LoyalistStrategy(this)
            } else {
                stra = RebelStrategy(this)
            }
        }


        if (stra != null) {
            (strategy as SunQuanStrategy).identityStrategy = stra
        }
    }


//    override fun beingAttacked(sender: General, suit: String) {
//        val hDC = hasDodgeCard()
//        val hasDoge = hasDodgeCard()
//        println("$name being attacked.")
//        if (sender is XuChu) {
//            if (sender.atk) {
//                if (hDC) {
//                    dodge()
//                } else {
//                    currentHP -= 2
//                    if(currentHP <= 0){
//                        println("$name is attacked successfully, current HP is 0")
//                    }else{
//                        println("$name is attacked successfully, current HP is $currentHP")
//                    }
//                }
//                if (player is Lord) {
//                    lord?.notifyObservers(hDC)
//                }
//            }
//        }
//        if (sender.lvbu) {
//            val hDC1 = hasTwoDodgeCard()
//            if (hDC1) {
//                dodge()
//                dodge()
//                println("[unrivaled] totally need to spending two doge card")
//            } else {
//                currentHP -= 1
//                if(currentHP <= 0){
//                    println("$name is attacked successfully, current HP is 0")
//                }else{
//                    println("$name is attacked successfully, current HP is $currentHP")
//                }
//            }
//            if (player is Lord) {
//                lord?.notifyObservers(hDC)
//            }
//        }
//        if (hasDoge) {
//            if (eight1 == true) {
//                println("[Eight Trigrams Formation] helps ${name} dodge.")
//                eight1 = false
//            } else {
//                println("[Eight Trigrams Formation] cannot help ${name} dodge.")
//                dodge()
//                println("$name dodged attack by spending a DODGE card.")
//            }
//        } else {
//            currentHP -= 1
//            if(currentHP <= 0){
//                println("$name is attacked successfully, current HP is 0")
//            }else{
//                println("$name is attacked successfully, current HP is $currentHP")
//            }
//
//        }
//
//        if (player is Lord) {
//            lord?.notifyObservers(hasDoge)
//        }
//
//        if (currentHP <= 0) {
//            alive = false
//            killer = sender
//            currentHP = 0
//            println("$name's hp is $currentHP now!")
//        }
//    }
//
}

class SunShangXiang(player: Player) : WuGeneral(player) {
    override var maxHP = 3
    override var name: String = "Sun Shang Xiang"
    override var gender = "Female"

    init {
        initializeIdentityStrategy()


    }

    fun initializeIdentityStrategy() {// For generals who have special strategy, this helps
        // implement their action that is affected by their
        //identity instead of their general skill
        strategy = SunShangXiangStrategy(this)
        var stra: Strategy? = null
        if (player is Rebel) {
            stra = RebelStrategy(this)
        } else if (player is Lord) {
            stra = LoyalistStrategy(this)
        } else if (player is Loyalist) {
            stra = LoyalistStrategy(this)
        } else if (player is Spy) {
            if ((player as? Spy)!!.isRevealed == false) {
                stra = LoyalistStrategy(this)
            } else {
                stra = RebelStrategy(this)
            }
        }


        if (stra != null) {
            (strategy as SunShangXiangStrategy).identityStrategy = stra
        }
    }


    override fun loseEquipment(): Card? {//Daredevil
        var count = 0
        var toRemove: Card? = null
        if (weapon != null) {
            weapon = null
            toRemove = weapon
        } else if (defense != null) {
            defense = null
            toRemove = defense
        } else {
            var equipment1 = equipment.toMutableList()
            for (eq in equipment1) {
                if (count < 1) {
                    equipment.remove(eq)


                    count++


                }
                toRemove = eq
            }
        }

        for (i in 1..2) {

            addDeck()
            val card = deck.removeAt(0)
            hand.add(card)
            card.initializer = this
        }
        print("[Daredevil]$name draw 2 card(s), now has ${hand.size} card(s). Hand: ")
        showHand()
        return toRemove


    }


    override fun beingAttacked(sender: General, suit: String) {
        println("$name being attacked.")
        if (sender.lvbu) {
            if (hasTwoDodgeCard()) {
                var count = 2
                if (eight1 == true) {
                    println("[Eight Trigrams Formation] helps ${name} dodge.")
                    eight1 = false
                    count--
                }
                if (eight2 == true) {
                    println("[Eight Trigrams Formation] helps ${name} dodge.")
                    eight2 = false
                    count--
                }
                if (count == 2) {
                    println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                }
                for (i in 0 until count) {
                    dodge()
                }
                println("[unrivaled] $name totally need to spending two doge card")
            } else {
                currentHP -= 1
                if (currentHP <= 0) {
                    println("$name is attacked successfully, current HP is 0")
                } else {
                    println("$name is attacked successfully, current HP is $currentHP")
                }
            }
            if (player is Lord) {
                lord?.notifyObservers(hasDodgeCard())
            }
        } else if (sender is XuChu) {
            if (sender.atk) {
                val hDC = hasDodgeCard()
                if (hDC) {
                    if (eight1 == true) {
                        println("[Eight Trigrams Formation] helps ${name} dodge.")
                        eight1 = false
                    } else {
                        if (defense is Eight_Trigrams_Formation) {
                            println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                        }
                        dodge()
                    }
                } else {
                    currentHP -= 2
                    if (currentHP <= 0) {
                        println("$name is attacked successfully, current HP is 0")
                    } else {
                        println("$name is attacked successfully, current HP is $currentHP")
                    }
                }
                if (player is Lord) {
                    lord?.notifyObservers(hDC)
                }
            }
        } else {
            if (hasDodgeCard()) {
                if (eight1 == true) {
                    println("[Eight Trigrams Formation] helps ${name} dodge.")
                    eight1 = false
                } else {
                    if (defense is Eight_Trigrams_Formation)
                        println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                    dodge()
                }
            } else {
                currentHP -= 1
                if (currentHP <= 0) {
                    println("$name is attacked successfully, current HP is 0")
                } else {
                    println("$name is attacked successfully, current HP is $currentHP")
                }
            }
            if (player is Lord) {
                lord?.notifyObservers(hasDodgeCard())
            }
        }
        if (currentHP <= 0) {
            alive = false
            killer = sender
            currentHP = 0
            println("$name's hp is $currentHP now!")
        }
    }

    fun betrothment(target: General) {
        if (hand.size < 2 || currentHP == maxHP) {// only work when have more than 2 cards
            return
        }


        for (i in 1..2) {
            val random = hand.random()
            hand.remove(random)
            discardDeck.add(random)
        }
        currentHP++
        target.currentHP++
        if (target.currentHP > target.maxHP) {
            target.currentHP = target.maxHP
        }
        println(
            "[Betrothment] ${name} discard two cards, ${name} and ${target.name} recover 1 health point," +
                    " current HP are ${currentHP} and ${target.currentHP}"
        )
    }


}


class LvMeng(player: Player) : WuGeneral(player) {
    override var maxHP = 4
    override var name: String = "Lv Meng"
    var hasSpentAttack = false


    init {
        initializeIdentityStrategy()
    }

    fun initializeIdentityStrategy() {// For generals who have special strategy, this helps
        // implement their action that is affected by their
        //identity instead of their general skill
        strategy = LvMengStrategy(this)
        var stra: Strategy? = null
        if (player is Rebel) {
            stra = RebelStrategy(this)
        } else if (player is Lord) {
            stra = LoyalistStrategy(this)
        } else if (player is Loyalist) {
            stra = LoyalistStrategy(this)
        } else if (player is Spy) {
            if ((player as? Spy)!!.isRevealed == false) {
                stra = LoyalistStrategy(this)
            } else {
                stra = RebelStrategy(this)
            }
        }


        if (stra != null) {
            (strategy as LvMengStrategy).identityStrategy = stra
        }
    }

    override fun discard(num: Int?) {
        if (!hasSpentAttack) {
            println("[Self-Control] $name doesn't attack anyone in this turn, he do not need to discard any card")
            super.discard(0)
        } else {
            super.discard(num)
        }
    }


    override fun beingAttacked(sender: General, suit: String) {
        println("$name being attacked.")
        if (sender.lvbu) {
            if (hasTwoDodgeCard()) {
                var count = 2
                if (eight1 == true) {
                    println("[Eight Trigrams Formation] helps ${name} dodge.")
                    eight1 = false
                    count--
                }
                if (eight2 == true) {
                    println("[Eight Trigrams Formation] helps ${name} dodge.")
                    eight2 = false
                    count--
                }
                if (count == 2) {
                    println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                }
                for (i in 0 until count) {
                    dodge()
                }
                println("[unrivaled]$name totally need to spending two doge card")
            } else {
                currentHP -= 1
                if (currentHP <= 0) {
                    println("$name is attacked successfully, current HP is 0")
                } else {
                    println("$name is attacked successfully, current HP is $currentHP")
                }
            }
            if (player is Lord) {
                lord?.notifyObservers(hasDodgeCard())
            }
        } else if (sender is XuChu) {
            if (sender.atk) {
                val hDC = hasDodgeCard()
                if (hDC) {
                    if (eight1 == true) {
                        println("[Eight Trigrams Formation] helps ${name} dodge.")
                        eight1 = false
                    } else {
                        if (defense is Eight_Trigrams_Formation) {
                            println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                        }
                        dodge()
                    }
                } else {
                    currentHP -= 2
                    if (currentHP <= 0) {
                        println("$name is attacked successfully, current HP is 0")
                    } else {
                        println("$name is attacked successfully, current HP is $currentHP")
                    }
                }
                if (player is Lord) {
                    lord?.notifyObservers(hDC)
                }
            }
        } else {
            if (hasDodgeCard()) {
                if (eight1 == true) {
                    println("[Eight Trigrams Formation] helps ${name} dodge.")
                    eight1 = false
                } else {
                    if (defense is Eight_Trigrams_Formation)
                        println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                    dodge()
                }
            } else {
                currentHP -= 1
                if (currentHP <= 0) {
                    println("$name is attacked successfully, current HP is 0")
                } else {
                    println("$name is attacked successfully, current HP is $currentHP")
                }

            }
            if (player is Lord) {
                lord?.notifyObservers(hasDodgeCard())
            }
        }
        if (currentHP <= 0) {
            alive = false
            killer = sender
            currentHP = 0
            println("$name's hp is $currentHP now!")
        }
    }

}

class HuangGai(player: Player) : WuGeneral(player) {
    override var maxHP = 4
    override var name: String = "Huang Gai"


    init {
        initializeIdentityStrategy()
    }

    fun initializeIdentityStrategy() {// For generals who have special strategy, this helps
        // implement their action that is affected by their
        //identity instead of their general skill
        strategy = HuangGaiStrategy(this)
        var stra: Strategy? = null
        if (player is Rebel) {
            stra = RebelStrategy(this)
        } else if (player is Lord) {
            stra = LoyalistStrategy(this)
        } else if (player is Loyalist) {
            stra = LoyalistStrategy(this)
        } else if (player is Spy) {
            if ((player as? Spy)!!.isRevealed == false) {
                stra = LoyalistStrategy(this)
            } else {
                stra = RebelStrategy(this)
            }
        }
        if (stra != null) {
            (strategy as HuangGaiStrategy).identityStrategy = stra
        }
    }


    fun sacrifice() {
        if (currentHP < 2)
            return
        currentHP--
        println("[Sacrifice] ${name} lose one HP and draw 2 cards")
        draw(2)


    }

}

abstract class WuGeneral(player: Player) : General(player) {
    var canActivateEntourageList: MutableList<Boolean> = mutableListOf()
    var canActivateEntourage = false

}