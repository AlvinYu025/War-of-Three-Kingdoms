package Generals

import Cards.Eight_Trigrams_Formation
import DiaoChanStrategy
import GeneralManager.addDeck
import GeneralManager.deck
import GeneralManager.discardDeck
import HuatuoStrategy
import Lord
import Loyalist
import LoyalistStrategy
import Player
import Rebel
import RebelStrategy
import Spy
import Strategy


var Neutral: MutableList<General> = mutableListOf()
var countNe = 0

var current: NeutralGeneral? = null
var headNe: NeutralGeneral? = null

abstract class NeutralGeneral(player: Player) : General(player) {
    var nextNe: NeutralGeneral? = null

}


class DiaoChan(player: Player) : NeutralGeneral(player) {
    override var gender = "Female"
    override var maxHP = 3
    override var name: String = "Diao Chan"

    init {
        initializeIdentityStrategy()
    }

    fun initializeIdentityStrategy() {// For generals who have special strategy, this helps
        // implement their action that is affected by their
        //identity instead of their general skill
        strategy = DiaoChanStrategy(this)
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
            (strategy as DiaoChanStrategy).identityStrategy = stra
        }
    }


    /*Choose to implement which one down here*/
    override fun discard(num: Int?) {
        var diff = hand.size - currentHP
        if (diff > 0 && hand.size > diff) {
            for (i in 0 until diff) {
                if (hand.size > 0) {
                    val random = hand.random()
                    hand.remove(random)
                    discardDeck.add(random)
                }
            }
            println("$name discarded $diff card(s), now has ${hand.size} card(s)")
            showHand()
        } else {
            var diff = hand.size - currentHP
            if (diff > 0 && hand.size != 0) {
                for (i in 0 until diff) {
                    if (hand.size > 0) {
                        val random = hand.random()
                        hand.remove(random)
                        discardDeck.add(random)
                    }
                }
                print("$name (HP: $currentHP) discarded $diff card(s), now has ${hand.size} card(s)")
                showHand()
            }
        }

        addDeck()
        val card = deck.removeAt(0)
        hand.add(card)
        card.initializer = this
        println("[Beauty Outshining the Moon] Diao Chan draw one more card, now has ${hand.size} card(s). Hand: ")
        showHand()
    }

}

class HuaXiong(player: Player) : NeutralGeneral(player) {
    override var gender = "Male"
    override var maxHP = 6
    override var name: String = "Hua Xiong"

    var l1: MutableList<Boolean> = mutableListOf(true, false)

    override fun beingAttacked(sender: General, suit: String) {
        println("$name being attacked.")
        if (sender is XuChu) {
            if (sender.atk) {
                val hDC = hasDodgeCard()
                if (hDC) {
                    if (eight1 == true) {
                        println("[Eight Trigrams Formation] helps ${name} dodge.")
                        eight1 = false
                    } else {
                        if (defense is Eight_Trigrams_Formation)
                            println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                        dodge()
                    }
                } else {
                    currentHP -= 2
                    if (currentHP <= 0) {
                        println("$name is attacked successfully, current HP is 0")
                    } else {
                        println("$name is attacked successfully, current HP is $currentHP")
                    }
                    if (suit.compareTo("Diamond") == 0 || suit.compareTo("Heart") == 0) {
                        if (l1.random()) {
                            sender.currentHP += 1
                            var na = sender.name
                            println("[Hua xiong passive skill] $na recover 1 hp")
                        } else {
                            sender.draw(1)
                            var na = sender.name
                            println("[Hua xiong passive skill] $na draw 1 card")
                        }
                    }
                }
                if (player is Lord) {
                    lord?.notifyObservers(hDC)
                }
            }
        } else if (sender.lvbu) {
            val hDC1 = hasTwoDodgeCard()
            if (hDC1) {
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
                lord?.notifyObservers(hDC1)
            }
        } else {
            if (hasDodgeCard()) {
                if (eight1 == true) {
                    println("[Eight Trigrams Formation] helps ${name} dodge.")
                    eight1 = false
                } else {
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
                if (suit.compareTo("Diamond") == 0 || suit.compareTo("Heart") == 0) {
                    if (l1.random()) {
                        sender.currentHP += 1
                        var na = sender.name
                        println("[Hua xiong passive skill] $na recover 1 hp")
                    } else {
                        sender.draw(1)
                        var na = sender.name
                        println("[Hua xiong passive skill] $na draw 1 card")
                    }
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

class Huatuo(player: Player) : NeutralGeneral(player) {
    override var gender = "Male"
    override var maxHP = 3
    override var name: String = "Hua tuo"

    init {
        initializeIdentityStrategy()
    }

    fun initializeIdentityStrategy() {// For generals who have special strategy, this helps
        // implement their action that is affected by their
        //identity instead of their general skill
        strategy = HuatuoStrategy(this)
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
            (strategy as HuatuoStrategy).identityStrategy = stra
        }
    }
}




