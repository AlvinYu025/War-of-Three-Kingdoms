package Generals

import Cards.*
import GeneralManager
import GeneralManager.addDeck
import GeneralManager.deck
import GeneralManager.discardDeck
import GeneralManager.generals
import Lord
import Player

class ZhenJi(player: Player) : WeiGeneral(player) {
    override var gender = "Female"
    override var maxHP = 3
    override var name: String = "Zhen Ji"
    override var nextGeneral: WeiGeneral? = null
    private var useBlackSuit = false
    override var hasPreSkill = true
    var dodgeCardNum = 0
    var dodgeCardList: MutableList<Card> = mutableListOf()

    override fun preSkill() {
        godessOfLuoRiver()
    }


    private fun godessOfLuoRiver() {
        var card: Card? = null
        while (hand.size < 5) {
            addDeck()
            if(deck.size>0){
                card = deck.removeAt(0)
                if (card.suit == "Spade" || card.suit == "Club") {
                    hand.add(card)
                    println("[Godess of Luo River] Zhen Ji draws ${suitMap[card.suit]} ${card.rank} ${card.name}")
                    print("Zhen Ji Hand: ")
                    showHand()
                } else {
                    break
                }
            }
        }
        if (card != null) {
            if (card.suit != "Spade" && card.suit != "Club") {
                discardDeck.add(card)
                println("[Godess of Luo River] The card's suit \"${suitMap[card.suit]}\" is not black!")
            }
        }
    }

    override fun hasDodgeCard(): Boolean {
        if(defense is Eight_Trigrams_Formation){
            eight1 = (defense as Eight_Trigrams_Formation).execute(this,null)
        }
        if(eight1 == true){
            return true
        }

        useBlackSuit = false
        var bool = false
        for (h in hand) {
            if (h is Dodge) {
                bool = true
            } else if (h.suit == "Spade" || h.suit == "Club") {
                useBlackSuit = true
                bool = true
            }
        }
        return bool
    }

    fun empressDowager() {
        for (card in hand) {
            var isDodgeCard = false
            if (card is Dodge) {
                isDodgeCard = true
                dodgeCardNum++
                dodgeCardList.add(card)
            }
            if (!isDodgeCard) {
                if (card.suit!!.compareTo("Spade") == 0 || card.suit!!.compareTo("Club") == 0) {
                    dodgeCardNum++
                    dodgeCardList.add(card)
                    println("[Empress dowager] Zhen Ji play ${suitMap[card.suit]} ${card.rank} ${card.name} as a Dodge card")
                }
            }
        }
    }
    override fun beingAttacked(sender: General, suit: String) {
        empressDowager()
        val hDC = hasDodgeCard()
        println("$name being attacked.")
        if (sender is XuChu) {
            if (sender.atk) {
                if (dodgeCardNum > 0 && dodgeCardList.size > 0) {
                    if (eight1 == true) {
                        println("[Eight Trigrams Formation] helps ${name} dodge.")
                        eight1 = false
                    } else {
                        if (defense is Eight_Trigrams_Formation) {
                            println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                        }

                        val c1 = dodgeCardList.random()
                        dodgeCardList.remove(c1)
                        hand.remove(c1)
                        discardDeck.add(c1)
                        println("$name dodged attack by spending ${suitMap[c1.suit]} ${c1.rank} ${c1.name}.")
                    }
                } else {
                    currentHP -= 2
                    if(currentHP <= 0){
                        println("$name is attacked successfully, current HP is 0")
                    }else{
                        println("$name is attacked successfully, current HP is $currentHP")
                    }

                }
                if (player is Lord) {
                    lord?.notifyObservers(hDC)
                }
            }
        } else if (sender.lvbu) {
            val hdc1 = hasTwoDodgeCard()
            if (dodgeCardNum >= 2 && dodgeCardList.size >= 2) {
                if (eight1 == true) {
                    println("[Eight Trigrams Formation] helps $name dodge.")
                    eight1 = false
                } else {
                    var c1 = dodgeCardList.random()
                    dodgeCardList.remove(c1)
                    hand.remove(c1)
                    discardDeck.add(c1)
                }

                var c2 = dodgeCardList.random()
                dodgeCardList.remove(c2)
                hand.remove(c2) //not yet prove
                discardDeck.add(c2)
                println("[unrivaled]$name totally need to spending two card")
            } else if (dodgeCardNum == 1 && dodgeCardList.size >= 1 && eight1) {
                println("[Eight Trigrams Formation] helps $name dodge.")
                eight1 = false

                var c2 = dodgeCardList.random()
                dodgeCardList.remove(c2)
                hand.remove(c2) //not yet prove
                discardDeck.add(c2)
                println("[unrivaled]$name totally need to spending two card")
            } else {
                currentHP -= 1
                if (currentHP <= 0) {
                    println("$name is attacked successfully, current HP is 0")
                } else {
                    println("$name is attacked successfully, current HP is $currentHP")
                }

            }
            if (player is Lord) {
                lord?.notifyObservers(hdc1)
            }
        } else {
            val hDC = hasDodgeCard()
            if (dodgeCardNum > 0 && dodgeCardList.size > 0) {
                if (eight1 == true) {
                    println("[Eight Trigrams Formation] helps $name dodge.")
                    eight1 = false
                } else {
                    if (defense is Eight_Trigrams_Formation) {
                        println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                    }

                    var c1 = dodgeCardList.random()
                    dodgeCardList.remove(c1)
                    hand.remove(c1)
                    discardDeck.add(c1)
                    println("$name dodged attack by spending ${suitMap[c1.suit]} ${c1.rank} ${c1.name}.")
                }
            } else {
                currentHP -= 1
                if(currentHP <= 0){
                    currentHP = 0
                }
                println("$name is attacked successfully, current HP is $currentHP")
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

        dodgeCardNum = 0
        dodgeCardList.clear()
    }
}


class CaoCao(player: Player) : WeiGeneral(player) {
    override var maxHP = 5
    override var name: String = "Cao Cao"

    override var nextGeneral: WeiGeneral? = null

    fun treachery(cardDealtDamage: Card) {
        var lock = 0
        val copyDiscardDeck = discardDeck.toMutableList()
        for (c in copyDiscardDeck) {
            if (c == cardDealtDamage && lock == 0) {
                hand.add(c)
                discardDeck.remove(c)
                lock++
                print("[Treachery] Cao Cao obtains the ${suitMap[c.suit]} ${c.rank} ${c.name} card dealt damage to him! Hand: ")
            }
        }
        showHand()
    }

    fun entourage(sender: General): Boolean {
        if (nextGeneral != null) {

            nextGeneral!!.canHelpLord(sender)

            canActivateEntourageList.forEach {
                if (it) {
                    println("[Entourage] Cao Cao activates Lord Skill Entourage.")
                    return true
                }
            }
            println("No Wei General can help Cao Cao.")
            return false
        } else {
            println("No Wei General as Loyalist in the game.")
            return false
        }
    }

    override fun beingAttacked(sender: General, suit: String) {
        println("$name being attacked.")
        if (entourage(sender) && !eight1) {
            this.handleAttack(sender)
        } else {
            if (sender is XuChu) {
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
                        if(currentHP <= 0){
                            println("$name is attacked successfully, current HP is 0")
                        }else{
                            println("$name is attacked successfully, current HP is $currentHP")
                        }
                    }
                    if (player is Lord) {
                        lord?.notifyObservers(hDC)
                    }
                }
            } else if (sender.lvbu) {
                if (hasTwoDodgeCard()) {
                    var count = 2
                    if(eight1 == true){
                        println("[Eight Trigrams Formation] helps ${name} dodge.")
                        eight1 = false
                        count--
                    }
                    if(eight2 == true){
                        println("[Eight Trigrams Formation] helps ${name} dodge.")
                        eight2 = false
                        count--
                    }
                    if(count == 2){
                        println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                    }
                    for(i in 0 until count ){
                        dodge()
                    }
                    println("[unrivaled] totally need to spending two doge card")
                } else {
                    currentHP -= 1
                    if(currentHP <= 0){
                        println("$name is attacked successfully, current HP is 0")
                    }else{
                        println("$name is attacked successfully, current HP is $currentHP")
                    }
                }
                if (player is Lord) {
                    lord?.notifyObservers(hasDodgeCard())
                }
            } else {
                if (hasDodgeCard()) {
                    if (eight1 == true) {
                        println("[Eight Trigrams Formation] helps ${name} dodge.")
                        eight1 = false
                    } else {
                        if(defense is Eight_Trigrams_Formation){
                            println("[Eight Trigrams Formation] cannot help ${name} dodge.")
                        }
                        dodge()
                    }
                } else {
                    currentHP -= 1
                    if(currentHP <= 0){
                        println("$name is attacked successfully, current HP is 0")
                    }else{
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

}

class XuChu(player: Player) : WeiGeneral(player) {
    override var maxHP = 4
    override var name: String = "Xu Chu"
    override var nextGeneral: WeiGeneral? = null
    var atk = false
    var due = false

    fun baredBodied(): Boolean {
        var result = false
        atk = false
        due = false

        for (c in hand) {
            if (c is Attack) {
                atk = true
            }
            if (c is Duel) {
                due = true
            }
        }
        if (atk) {
            result = true
        }
        if (due) {
            result = true
        }
        return result
    }

    override fun draw(num: Int?) {
        if (baredBodied()) {
            println("[Bared Bodied] Xu Chu draw 1 less card, his Attack or Duel deal 1 more damage!")

            addDeck()
            val card = deck.removeAt(0)
            hand.add(card)
            card.initializer = this

            print("$name draw 1 card(s), now has ${hand.size} card(s). Hand: ")
            showHand()
        } else if (num != null) {
            //special draw
            for (i in 1..num) {

                    addDeck()
                val card = deck.removeAt(0)
                hand.add(card)
                card.initializer = this
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
                val card = deck.removeAt(0)
                hand.add(card)
                card.initializer = this
            }
            print("$name draw $drawCards card(s), now has ${hand.size} card(s). Hand: ")
            showHand()
        }
    }
}

class ZhangLiao(player: Player) : WeiGeneral(player) {
    override var maxHP = 4
    override var name: String = "Zhang Liao"
    override var nextGeneral: WeiGeneral? = null

    var newGenerals = generals.toMutableList()
    var geOne = false
    var geTwo = false
    val nonce = 0.5

    fun incursion() {
        val generalList: MutableList<Boolean> = mutableListOf()
        var trueCount = 0
        for (general in newGenerals) {
            if (general is ZhangLiao) {
                newGenerals.remove(general)
            }
            else if (general.hand.size > 0) {
                generalList.add(true)
            }
        }
        for (bool in generalList) {
            if (bool) {
                trueCount++
            }
        }
        if (trueCount >= 1) {
            geOne = true
        }
        if (trueCount >= 2) {
            geTwo = true
        }
    }
    override fun draw(num: Int?) {
        incursion()
        val mood =Math.random()
        if (mood >= nonce && GeneralManager.turns != 0) {
            var count = 0
            if (geTwo) {
                for (general in newGenerals) {
                    if (general.hand.size > 0 && count < 2) {
                        val card = general.hand.random()
                        general.hand.remove(card)
                        hand.add(card)
                        count++
                        println("[Incursion] $name steals one card ${suitMap[card.suit]} ${card.rank} ${card.name} from ${general.name}.")
                    }
                }
            } else if (geOne) {
                for (general in newGenerals) {
                    if (general.hand.size > 0 && count < 1) {
                        val card = general.hand.random()
                        general.hand.remove(card)
                        hand.add(card)
                        count++
                        println("[Incursion] $name steals one card ${suitMap[card.suit]} ${card.rank} ${card.name} from ${general.name}.")
                    }
                }
            }
            print("$name Hand: " )
            showHand()
        } else if (num != null) {
            //special draw
            for(i in 1..num){

                addDeck()
                val card = deck.removeAt(0)
                hand.add(card)
                card.initializer = this
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
                val card = deck.removeAt(0)
                hand.add(card)
                card.initializer = this
            }

            print("$name draw $drawCards card(s), now has ${hand.size} card(s). Hand: ")
            showHand()
        }
    }
}

abstract class WeiGeneral(player: Player) : General(player) {
    var canActivateEntourageList: MutableList<Boolean> = mutableListOf()
    var canActivateEntourage = false
    open var headGeneral: WeiGeneral? = null
    abstract var nextGeneral: WeiGeneral?

    open fun setNext(weiGeneral: WeiGeneral) {
        if (nextGeneral == null) {
            nextGeneral = weiGeneral
        } else {
            nextGeneral?.setNext(weiGeneral)
        }
    }

    open fun syncList() {
        if (nextGeneral != null) {
            nextGeneral!!.canActivateEntourageList = canActivateEntourageList
            nextGeneral!!.syncList()
        } else {
            headGeneral!!.canActivateEntourageList = canActivateEntourageList
        }
    }

    open fun canHelpLord(sender: General) {
        if (sender.lvbu) {
            if (hasTwoDodgeCard()) {
                canActivateEntourage = true
                canActivateEntourageList.add(true)
                syncList()
            } else if (nextGeneral != null) {
                canActivateEntourage = false
                canActivateEntourageList.add(false)
                syncList()
                nextGeneral!!.canHelpLord(sender)
            } else {
                canActivateEntourage = false
                canActivateEntourageList.add(false)
                syncList()
            }
        } else {
            if (hasDodgeCard()) {
                canActivateEntourage = true
                canActivateEntourageList.add(true)
                syncList()
            } else if (nextGeneral != null) {
                canActivateEntourage = false
                canActivateEntourageList.add(false)
                syncList()
                nextGeneral!!.canHelpLord(sender)
            } else {
                canActivateEntourage = false
                canActivateEntourageList.add(false)
                syncList()
            }

        }

    }

    open fun handleAttack(sender: General) {

        if (canActivateEntourage) {
            println("$name helps Cao Cao dodged an attack by spending a dodge card.")
            dodge()
        } else {
            if (nextGeneral != null) {
                nextGeneral?.handleAttack(sender)
            } else {
                if (headGeneral?.hasDodgeCard() == true) {
                    headGeneral?.dodge()
                    (headGeneral?.player as Lord).notifyObservers(true)
                } else {
                    headGeneral?.currentHP = headGeneral?.currentHP!! - 1
                    println("${headGeneral!!.name} can't dodge attack. Current HP - 1. Current HP is ${headGeneral!!.currentHP}.")
                    (headGeneral?.player as Lord).notifyObservers(false)

                    if (headGeneral!!.currentHP <= 0) {
                        headGeneral!!.alive = false
                        headGeneral!!.killer = sender
                        headGeneral!!.currentHP = 0
                        println("${headGeneral!!.name}'s hp is ${headGeneral!!.currentHP} now!")
                    }
                }
            }
        }
    }

}