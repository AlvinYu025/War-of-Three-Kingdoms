package Generals

import Cards.*
import Factory.delone
import GeneralManager
import GeneralManager.addDeck
import GeneralManager.deck
import GeneralManager.discardDeck
import Lord
import Player
import Strategy

abstract class General(val player: Player) : Player by player {
    abstract var name: String
    open var gender: String = "Male"
    open var isAcedia = false
    open var isSkip = false
    var commands: MutableList<CommandCard> = mutableListOf()// for judgement command cards
    val lord = player as? Lord
    open var strategy: Strategy? = null
    var attackRange = 1
    open var maxHP = 4
    open var lvbu = false
    open var hasPreSkill = false
    val suitMap = mapOf("Heart" to "♡", "Spade" to "♠", "Diamond" to "♦", "Club" to "♣")
    var alive = true
    var hashouseone = false
    var delhouseone = false
    var hasweapon = false
    var hasdefense = false
    var trueDead = false
    var killer: General? = null
    open var sun = false
    var eight1 = false
    var eight2 = false

    fun setJudgementCommand(command: CommandCard) {
        commands.add(command)
    }

    open fun preSkill() {}

    fun turn() {
        if (alive) {
            preparationPhase()
            judgementPhase()
            draw(2)
            playPhase()
            discard(null)
            finalPhase()
        }
    }

    fun preparationPhase() {
        if (hasPreSkill) {
            preSkill()
        }
    }

    fun judgementPhase() {
        var command1 = commands.toMutableList()
        for (command in command1) {

            println("$name judging the ${command.name} card.")
            command.execute()//the judgement card is removed from commands and discarded after it is executed

        }

    }

    open fun draw(num: Int?) {
        if (num != null) {
            //special draw
            for (i in 1..num) {
                addDeck()
                if (deck.size > 0) {
                    val card = deck.removeAt(0)
                    hand.add(card)
                    card.initializer = this
                }
            }
            print("$name draw $num card(s), now has ${hand.size} card(s). Hand: ")
            showHand()
        } else {
            var drawCards = 0
            //Initial draw
            if (GeneralManager.turns == 0) {
                drawCards = 4
            } else {
                //normal draw
                drawCards = 2
            }

            for (i in 0 until drawCards) {
                addDeck()
                val card = deck.removeAt(0) //Small chance of IndexOutOfBounds Error
                hand.add(card)
                card.initializer = this
                addDeck()
            }
            print("$name draw $drawCards card(s), now has ${hand.size} card(s). Hand: ")
            showHand()
        }
    }

    fun playPhase() {
        if (!isSkip) {
            strategy?.playNextCard()
        } else {
            isSkip = false
        }
    }

    open fun discard(num: Int?) {
        if (num != null) {
            //special discard
            for (i in 1..num) {
                if (hand.size > 0) {
                    val random = hand.random()
                    hand.remove(random)
                    discardDeck.add(random)
                }
            }
            print("$name discarded $num card(s), now has ${hand.size} card(s). Hand: ")
            showHand()
        } else {
            //discard at every turn
            val diff = hand.size - currentHP
            if (diff > 0 && hand.size > diff) {
                for (i in 0 until diff) {
                    if (hand.size > 0) {
                        val random = hand.random()
                        hand.remove(random)
                        discardDeck.add(random)
                    }
                }
                print("$name (HP: $currentHP) discarded $diff card(s), now has ${hand.size} card(s). Hand: ")
                showHand()
            }
        }
    }

    fun finalPhase() {

    }

    open fun loseEquipment(): Card? {// for SunShangXiang's special skill
        var count = 0
        if (weapon != null) {
            weapon = null
            return weapon
        }
        if (defense != null) {
            defense = null
            return defense
        }


        var equipment1 = equipment.toMutableList()
        for (eq in equipment1) {
            if (count < 1) {
                equipment.remove(eq)


                count++


            }
            var toRemove = eq
            return toRemove
        }
        return null
    }

    open fun hasDodgeCard(): Boolean {
        if (this.defense is Eight_Trigrams_Formation) {
            eight1 = (defense as Eight_Trigrams_Formation).execute(this, null)
        }
        if (eight1 == true) {
            return true
        } else {
            for (card in hand) {
                if (card is Dodge) {
                    return true
                }
            }
        }
        return false
    }

    open fun hasAttackCard(): Boolean {
        if (lvbu) {
            var cnt = 0
            for (card in hand) {
                if (card is Attack) {
                    cnt++
                }
            }
            return cnt >= 2
        } else {
            for (card in hand) {
                if (card is Attack) {
                    return true
                }
            }
        }
        return false
    }

    open fun hasTwoDodgeCard(): Boolean {
        var eightCount = 0
        if (this.defense is Eight_Trigrams_Formation) {
            eight1 = (defense as Eight_Trigrams_Formation).execute(this, null)
            eight2 = (defense as Eight_Trigrams_Formation).execute(this, null)
        }
        if (eight1 == true) {
            eightCount++
        }
        if (eight2 == true) {
            eightCount++
        }

        var cnt = 0
        for (card in hand) {
            if (card is Dodge) {
                cnt++
            }
        }
        return cnt + eightCount >= 2
    }

    open fun beingAttacked(sender: General, suit: String) {
        println("$name being attacked.")
        if (sender is XuChu) {
            if (sender.atk) {
                val hDC = hasDodgeCard()
                if (hDC) {
                    if (eight1 == true) {
                        println("${this.name}'s[Eight Trigrams Formation] helps ${name} dodge.")
                        eight1 = false
                    } else {
                        if (defense is Eight_Trigrams_Formation) {
                            println("${this.name}'s[Eight Trigrams Formation] cannot help ${name} dodge.")
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
        } else if (sender.lvbu) {
            val hDC1 = hasTwoDodgeCard()
            if (hDC1) {
                var count = 2
                if (eight1 == true) {
                    println("${this.name}'s[Eight Trigrams Formation] helps ${name} dodge.")
                    eight1 = false
                    count--
                }
                if (eight2 == true) {
                    println("${this.name}'s[Eight Trigrams Formation] helps ${name} dodge.")
                    eight2 = false
                    count--
                }
                if (count == 2) {
                    println("${this.name}'s[Eight Trigrams Formation] cannot help ${name} dodge at all.")
                }

                println("[unrivaled] $name totally need to spending two doge card")

                for (i in 0 until count) {
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
                lord?.notifyObservers(hDC1)
            }
        } else {
            val hDC = hasDodgeCard()
            if (hDC) {
                if (eight1 == true) {
                    println("${this.name}'s[Eight Trigrams Formation] helps ${name} dodge.")
                    eight1 = false
                } else {
                    if (defense is Eight_Trigrams_Formation) {
                        println("${this.name}'s[Eight Trigrams Formation] cannot help ${name} dodge.")
                    }
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
                lord?.notifyObservers(hDC)
            }
        }
        if (currentHP <= 0) {
            alive = false
            killer = sender
            currentHP = 0
            println("$name's hp is $currentHP now!")
        }
    }

    open fun beingAttacked_Rock_Cleaving_Axe(sender: General, suit: String) {
        val before = currentHP
        currentHP--
        val after = currentHP
        println("$name being attacked by Rock Cleaving Axe. HP becomes from ${before} to ${after}.")
        if (currentHP <= 0) {
            alive = false
            killer = sender
            currentHP = 0
            println("$name's hp is $currentHP now!")
        }
    }


    open fun dodge() {
        var dodgeCard: Card = Dodge(null, null)
        for (card in hand) {
            if (card is Dodge) {
                dodgeCard = card
            }
        }
        hand.remove(dodgeCard) //not yet prove
        discardDeck.add(dodgeCard)
        println("$name spend a DODGE card ${suitMap[dodgeCard.suit]} ${dodgeCard.rank} ${dodgeCard.name}. Current cards: ${hand.size}")
    }


    open fun spendAttackCard() {
        if (lvbu) {
            println("[unrivaled] $name totally need to spending two attack card")

            for (i in 0..1) {
                var isSpent = false
                var hand1 = hand.toMutableList()
                for (card in hand1) {
                    if (!isSpent && card is Attack) {

                        hand.remove(card)
                        discardDeck.add(card)
                        isSpent = true
                    }
                }
            }
        } else {
            var isSpent = false
            var hand1 = hand.toMutableList()
            for (card in hand) {
                if (!isSpent && card is Attack) {
                    hand1.remove(card)
                    discardDeck.add(card)
                    isSpent = true
                }
            }
            hand = hand1
        }
    }

    open fun spendAttackCard1() {
        var isSpent = false
        var hand1 = hand.toMutableList()
        for (card in hand) {
            if (!isSpent && card is Attack) {
                hand1.remove(card)
                discardDeck.add(card)
                isSpent = true
            }
        }
        hand = hand1
    }

    open fun spendDodgeCard() {
        var dodgeCard: Card = Dodge(null, null)
        if (this is ZhenJi) {
            empressDowager()
            if (dodgeCardNum > 0 && dodgeCardList.size > 0) {
                dodgeCard = dodgeCardList.random()
                dodgeCardList.remove(dodgeCard)
                hand.remove(dodgeCard)
                discardDeck.add(dodgeCard)
                println("$name spend a DODGE card ${suitMap[dodgeCard.suit]} ${dodgeCard.rank} ${dodgeCard.name} to avoid a defect.")
            }
            dodgeCardList.clear()
        } else {
            for (card in hand) {
                if (card is Dodge) {
                    dodgeCard = card
                }
            }
            hand.remove(dodgeCard)
            discardDeck.add(dodgeCard)
            println("$name spend a DODGE card ${suitMap[dodgeCard.suit]} ${dodgeCard.rank} ${dodgeCard.name} to avoid a defect.")
        }
    }


    fun showHand() {
        val suitMap = mapOf("Heart" to "♡", "Spade" to "♠", "Diamond" to "♦", "Club" to "♣")

        hand.forEach {
            if (it == hand[0]) {
                print(suitMap[it.suit] + " " + it.rank + " " + it.name)
            } else {
                print(", " + suitMap[it.suit] + " " + it.rank + " " + it.name)
            }
        }
        println()
    }


    fun hasEquipment(): Boolean {
        if (equipment.size > 0 || weapon != null || defense != null)
            return true
        return false
    }


    fun hasNegate(): Negate? {
        for (handcard in hand) {
            if (handcard is Negate) {

                return handcard
            }
        }
        return null
    }
}

fun checkDistance(sender: General, receiver: General): Int {
    var distance: Int = 0
    var location: Int = 0
    var location1: Int = 0

    for (i in 0..GeneralManager.generals.size - 1) {
        if (sender == GeneralManager.getGeneral(i)) {
            location = i
        }
        if (receiver == GeneralManager.getGeneral(i)) {
            location1 = i
        }
    }

    var dist: Int = location - location1

    if (dist < 0) {
        dist *= -1
    }

    distance = if (GeneralManager.generals.size % 2 == 0) {
        if (dist < (GeneralManager.generals.size) / 2) {
            dist
        } else {
            GeneralManager.generals.size - dist
        }
    } else {
        if (dist < (GeneralManager.generals.size) / 2 + 1) {
            dist
        } else {
            GeneralManager.generals.size - dist
        }
    }

    var addone: Boolean = false
    var delone1: Boolean = false
    if (sender.equipment.size > 0) {
        for (card in sender.equipment) {
            for (card1 in delone) {
                if (card.name.compareTo(card1) == 0) {
                    delone1 = true
                    break
                }
            }
        }
    }

    if (receiver.equipment.size > 0) {
        for (card in receiver.equipment) {
            if (card.name.compareTo("Shadowrunner") == 0) {
                addone = true
                break
            }
        }
    }


    if (addone && !delone1) {
        distance += 1
    } else if (!addone && delone1) {
        distance -= 1
    }

    return distance
}

fun checkRange(general: General) {
    if (general.weapon != null) {
        general.attackRange = general.weapon!!.distance
    }
}

class LvBu(player: Player) : General(player) {
    override var maxHP = 4
    override var name: String = "Lv Bu"
    override var lvbu = true

}




