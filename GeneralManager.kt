import Cards.Card
import Factory.*

import Generals.General

object GeneralManager {
    val cardRank = mapOf(
        "A" to 1,
        "2" to 2,
        "3" to 3,
        "4" to 4,
        "5" to 5,
        "6" to 6,
        "7" to 7,
        "8" to 8,
        "9" to 9,
        "10" to 10,
        "J" to 11,
        "Q" to 12,
        "K" to 13
    )
    var turns = 0

    var generals: MutableList<General> = mutableListOf()
    var deck: MutableList<Card> = mutableListOf()
    var discardDeck: MutableList<Card> = mutableListOf()

    fun cardGenerator() {
        val basicCardFactory = BasicCardFactory()
        val commandCardFactory = CommandCardFactory()
        val weaponFactory = WeaponFactory()
        val defenseFactory = DefenseFactory()
        val horseCardFactory = HorseCardFactory()

        //basic
        for (i in 0 until 52) {
            val addingCard = basicCardFactory.createCard()
            if (i == 0) {
                addingCard.appearTimes++
                deck.add(addingCard)
            } else if (checkSame(addingCard)) {
                addingCard.appearTimes++
                deck.add(addingCard)
            }
        }
        for (i in 0 until commandCardList.size) {
            val addingCard = commandCardFactory.createCard()
            if (checkSame(addingCard)) {
                addingCard.appearTimes++
                deck.add(addingCard)
            }
        }
        for (i in 0 until weaponFactory.weaponCardList.size) {
            val addingCard = weaponFactory.createCard()
            if (checkSame(addingCard)) {
                addingCard.appearTimes++
                deck.add(addingCard)
            }
        }
        for (i in 0 until defenseFactory.defenseCardList.size) {
            val addingCard = defenseFactory.createCard()
            if (checkSame(addingCard)) {
                addingCard.appearTimes++
                deck.add(addingCard)
            }
        }
        for (i in 0 until HorseCardList.size) {
            val addingCard = horseCardFactory.createCard()
            if (checkSame(addingCard)) {
                addingCard.appearTimes++
                deck.add(addingCard)
            }
        }

        deck.shuffle()
    }

    fun checkSame(card: Card): Boolean {
        for (i in 0 until deck.size) {

            //basic cards check logic
            if (card.suit == deck[i].suit && card.rank == deck[i].rank) {
                if (card.appearTimes >= 2) {
                    return false
                } else if (card.suit == "Spade" && cardRank[card.rank]!! >= 8 && cardRank[card.rank]!! <= 10) { //spade 8 - 10 attack
                    if (card.appearTimes < 2) {
                        return true
                    }
                } else if (card.suit == "Club" && cardRank[card.rank]!! >= 8 && cardRank[card.rank]!! <= 11) { //club 8 - J attack
                    if (card.appearTimes < 2) {
                        return true
                    }
                } else if (card.suit == "Heart") {
                    if (cardRank[card.rank]!! == 2 || cardRank[card.rank]!! == 10) { //heart 2 or 10, dodge or attack
                        if (card.appearTimes < 2) {
                            return true
                        }
                    }
                } else if (card.suit == "Diamond") {
                    if (cardRank[card.rank]!! == 2 || cardRank[card.rank]!! == 11) { //diamond 2 or J, dodge
                        if (card.appearTimes < 2) {
                            return true
                        }
                    }
                }
                return false
            }
        }
        return true
    }

    fun addDeck() {
        val discardDeckCopy = discardDeck.toMutableList()
        if (deck.size < 10 && discardDeck.size > 0) {
            for (card in discardDeckCopy) {
                discardDeck.remove(card)
                if (card.suit != null && card.rank != null) {
                    deck.add(card)
                }
            }
        }
        deck.shuffle()
    }

    fun distributeStartingHand() {
        println()
        println("---Distribute Cards for the Starting Hand---")
        for (i in generals) {
            i.draw(null)
        }
        turns++
        println()
    }

    fun addGeneral(general: General) {
        generals.add(general)
    }

    fun getGeneral(num: Int): General {
        return generals.get(num)


    }

    fun getGeneralCount(): Int {
        return generals.size
    }

    fun removeGeneral(general: General) {
        generals.remove(general)
    }

    fun createGenerals(numOfGenerals: Int) {
        val lf = LordFactory()

        if (generals.size >= 10) throw IllegalArgumentException("At most 10 generals in a game")

        if (generals.size == 0 && numOfGenerals < 3) throw IllegalArgumentException("Must have at least 3 non lord generals")
        var total = numOfGenerals + generals.size

        if (generals.size == 0) {
            val lord = lf.createRandomGeneral(1)
            generals.add(lord)
            total = numOfGenerals - 1 + generals.size
        }


        val nf = NonLordFactory(generals.get(0), generals.get(0).player as Lord)


        for (i in generals.size + 1..total) {
            val gen = nf.createRandomGeneral(i)
            generals.add(gen)
        }
        println("Total number of players: ${generals.size}")
    }

    var winPrint = 0
    fun turns() {
        while (winPrint == 0) {

            println("---Turn $turns---")

            for (i in 0 until generals.size) {
                if (winPrint == 0) generals[i].turn()
            }
            turns++
            addDeck()

            val newGenerals = generals.toMutableList()

            for (general in newGenerals) {
                if (!general.alive) {
                    if (general.player is Rebel) {
                        for (i in 0 until 3) {
                            addDeck()
                            val card = deck.removeAt(0)
                            general.killer?.hand?.add(card)
                        }
                        println("${general.killer!!.name} kills ${general.name}, a ${general.player.identity}, rewards 3 additional cards!")
                        print("Now ${general.killer!!.name} has ${general.killer!!.hand.size} cards: ")
                        general.killer?.showHand()
                    } else if (general.player is Loyalist && general.killer?.player is Lord) {
                        val handCopy = general.killer?.hand!!.toMutableList()
                        for (card in handCopy) {
                            general.killer?.hand!!.remove(card)
                            discardDeck.add(card)
                        }
                    }

                    val newGeneralsHand = general.hand.toMutableList()
                    for (card in newGeneralsHand) {
                        general.hand.remove(card)
                        discardDeck.add(card)
                    }

                    generals.remove(general)
                    println("${general.name} (${general.player.identity}) is dead!")
                }
            }

            val newGenerals1 = generals.toMutableList()
            var lordAlive = false
            var rebelAlive = false
            var spyAlive = false
            for (general in newGenerals1) {
                if (general.player is Lord && general.alive) {
                    lordAlive = true
                }
                if (general.player is Spy && general.alive) {
                    spyAlive = true
                }
                if (general.player is Rebel && general.alive) {
                    rebelAlive = true
                }
            }
            if (!lordAlive) {
                if (rebelAlive) {
                    println("Rebel win, Game over!")
                    winPrint++
                    break
                } else if (spyAlive) {
                    println("Spy win, Game over!")
                    winPrint++
                    break
                } else {
                    println("This game is tied, Game over!")
                    winPrint++
                    break
                }
            } else {
                if (!rebelAlive && !spyAlive) {
                    println("Lord win, Game over!")
                    winPrint++
                    break
                } else if (turns > 50) {
                    print("Game Over! ")
                    println("Lord and Loyalist win!")
                    winPrint++
                    break
                }
            }

            println()
        }
    }

    fun startGame(numOfGenerals: Int) {
        createGenerals(numOfGenerals)
        cardGenerator()
        distributeStartingHand()
        turns()
    }
}

fun main() {
    GeneralManager.startGame(5)
}