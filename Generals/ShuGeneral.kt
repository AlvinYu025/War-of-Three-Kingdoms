package Generals

import Cards.Attack
import Cards.Card
import Cards.Dodge
import Cards.Eight_Trigrams_Formation
import GeneralManager.discardDeck
import Lord
import Loyalist
import LoyalistStrategy
import Player
import Rebel
import RebelStrategy
import Spy
import Strategy
import ZhaoYunStrategy

open class ShuGeneral(player: Player) : General(player) {
    override var name: String = ""
}

class LiuBei(player: Player) : ShuGeneral(player) {
    override var maxHP = 5
    override var name: String = "Liu bei"
    var shuList = mutableListOf<General>()

    fun addShu(general: General) {
        shuList.add(general)
    }

    fun removeShu(general: General) {
        shuList.remove(general)
    }

    fun checkAlive() {
        val shulistcopy = shuList.toMutableList()
        for (gen in shulistcopy) {
            if (gen.alive == false) {
                removeShu(gen)
            }
        }
    }

    fun RouseJudgement(): Boolean {
        if (shuList.size > 0) {
            println("[Rouse] Liu Bei can ask any Shu general that is in play to use an ATTACK card for him.")
            for (gen in shuList) {
                if (gen.hasAttackCard() && gen.shouldHelpLord()) {
                    if (gen is ZhaoYun) {
                        val hand1 = gen.hand
                        for (card in hand1) {
                            if (card is Attack) {
                                println("Shu general ${gen.name} can spend an ATTACK for Liu Bei.")
                                return true
                            }
                        }
                        for (card in hand1) {
                            if (card is Dodge) {
                                println("Shu general ${gen.name} can spend an ATTACK for Liu Bei.")
                                return true
                            }
                        }
                    } else {
                        val hand1 = gen.hand
                        for (card in hand1) {
                            if (card is Attack) {
                                println("Shu general ${gen.name} can spend an ATTACK for Liu Bei.")
                                return true
                            }
                        }
                    }
                } else {
                    println("Shu general: ${gen.name} cannot spend ATTACK card for Liu Bei.")
                }
            }
            return false
        } else {
            println("There are no Shu generals in this game.")
        }
        return false
    }

    fun Rouse(): Boolean {
        if (shuList.size > 0) {
            println("[Rouse] Liu Bei can ask any Shu general that is in play to use an ATTACK card for him.")
            for (gen in shuList) {
                if (gen.hasAttackCard() && gen.shouldHelpLord()) {
                    if (gen is ZhaoYun) {
                        val hand1 = gen.hand
                        for (card in hand1) {
                            if (card is Attack) {
                                gen.hand.remove(card)
                                discardDeck.add(card)
                                println("Shu general ${gen.name} spends an ATTACK for Liu Bei.")
                                return true
                            }
                        }
                        for (card in hand1) {
                            if (card is Dodge) {
                                gen.hand.remove(card)
                                discardDeck.add(card)
                                println("Shu general ${gen.name} spends an ATTACK for Liu Bei.")
                                return true
                            }
                        }
                    } else {
                        val hand1 = gen.hand
                        for (card in hand1) {
                            if (card is Attack) {
                                gen.hand.remove(card)
                                discardDeck.add(card)
                                println("Shu general ${gen.name} spends an ATTACK for Liu Bei.")
                                return true
                            }
                        }
                    }
                } else {
                    println("Shu general: ${gen.name} cannot spend ATTACK card for Liu Bei.")
                }
            }
            return false
        } else {
            println("There are no Shu generals in this game.")
        }
        return false
    }

    fun RouseAttack(): Card? {
        if (shuList.size > 0) {
            for (gen in shuList) {
                if (gen.hasAttackCard() && gen.shouldHelpLord()) {
                    if (gen is ZhaoYun) {
                        val hand1 = gen.hand
                        for (card in hand1) {
                            if (card is Attack) {
                                gen.hand.remove(card)
                                discardDeck.add(card)
                                println("Shu general ${gen.name} spends an ATTACK for Liu Bei.")
                                return card
                            }
                        }
                        for (card in hand1) {
                            if (card is Dodge) {
                                gen.hand.remove(card)
                                discardDeck.add(card)
                                println("Shu general ${gen.name} spends an ATTACK for Liu Bei.")
                                return card
                            }
                        }
                    } else {
                        val hand1 = gen.hand
                        for (card in hand1) {
                            if (card is Attack) {
                                gen.hand.remove(card)
                                discardDeck.add(card)
                                println("Shu general ${gen.name} spends an ATTACK for Liu Bei.")
                                return card
                            }
                        }
                    }
                } else {
                    println("Shu general: ${gen.name} cannot spend ATTACK card for Liu Bei.")
                }
            }
        } else {
            println("There are no Shu generals in this game.")
        }
        return null
    }

    override fun hasAttackCard(): Boolean {
        checkAlive()

        if (shuList.size > 0) {
            for (gen in shuList) {
                if (gen.hasAttackCard() && gen.shouldHelpLord())
                    return true
            }
        }
        for (card in hand) {
            if (card is Attack) {
                return true
            }
        }
        return false
    }

    override fun spendAttackCard() {
        if (lvbu) {
            for (i in 0..1) {
                val rouse = Rouse()
                if (rouse == false) {
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
            }
        } else {
            val rouse = Rouse()
            if (rouse == false) {
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
    }
}

class ZhangFei(player: Player) : ShuGeneral(player) {
    override var maxHP = 4
    override var name: String = "Zhang Fei"
}

class ZhaoYun(player: Player) : ShuGeneral(player) {
    init {
        initializeIdentityStrategy()
    }
    override var maxHP = 4
    override var name: String = "Zhao Yun"

    fun initializeIdentityStrategy() {// For generals who have special strategy, this helps
        // implement their action that is affected by their
        //identity instead of their general skill
        strategy = ZhaoYunStrategy(this)
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
            (strategy as ZhaoYunStrategy).identityStrategy = stra
        }
    }

    override fun hasDodgeCard(): Boolean {
        if (defense is Eight_Trigrams_Formation) {
            eight1 = (defense as Eight_Trigrams_Formation).execute(this, null)
        }
        if (eight1 == true) {
            return true
        }

        var activateSkill = true
        var result = false
        for (card in hand) {
            if (card is Dodge) {
                activateSkill = false
                result = true
            }
        }
        if (activateSkill == true) {
            for (card in hand) {
                if (card is Attack) {
                    result = true
                }
            }
        }
        return result
    }

    override fun hasAttackCard(): Boolean {
        var activateSkill = true
        var result = false
        for (card in hand) {
            if (card is Attack) {
                activateSkill = false
                result = true
            }
        }
        if (activateSkill == true) {
            for (card in hand) {
                if (card is Dodge) {
                    result = true
                }
            }
        }
        return result
    }

    override fun hasTwoDodgeCard(): Boolean {
        var eightCount = 0
        if (defense is Eight_Trigrams_Formation) {
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
        if (cnt < 2) {
            for (card in hand) {
                if (card is Attack) {
                    cnt++
                }
            }
        }
        return cnt + eightCount >= 2
    }

    override fun spendAttackCard() {
        if (lvbu) {
            println("[unrivaled]$name totally need to spending two attack card")
            val hand1 = hand.toMutableList()
            var attackCount = 0
            for (card in hand1) {
                if (card is Attack) {
                    hand.remove(card)
                    discardDeck.add(card)
                    attackCount++
                    if (attackCount == 2) {
                        break
                    }
                }
            }
            if (attackCount < 2) {
                println("[Dragon Heart] Zhao Yun's ATTACK and DODGE cards can be used interchangeably. (Dodge for Attack)")
                for (card in hand1) {
                    if (card is Dodge) {
                        hand.remove(card)
                        discardDeck.add(card)
                        attackCount++
                        if (attackCount == 2) {
                            break
                        }
                    }
                }
            }
        } else {
            val hand1 = hand.toMutableList()
            var activateSkill = true
            for (card in hand1) {
                if (card is Attack) {
                    hand.remove(card)
                    discardDeck.add(card)
                    activateSkill = false
                    break
                }
            }
            if (activateSkill == true) {
                for (card in hand1) {
                    if (card is Dodge) {
                        println("[Dragon Heart] Zhao Yun's ATTACK and DODGE cards can be used interchangeably. (Dodge for Attack)")
                        hand.remove(card)
                        discardDeck.add(card)
                        break
                    }
                }
            }
        }
    }

    override fun dodge() {
        if (eight1 == true) {
            println("${this.name}'s[Eight Trigrams Formation] helps ${name} dodge.")
            return
        }

        var hasDodge = false

        var dodgeCard: Card = Dodge(null, null)
        for (card in hand) {
            if (card is Dodge) {
                dodgeCard = card
                hasDodge = true
                break
            }
        }

        if (hasDodge == true) {
            hand.remove(dodgeCard)
            discardDeck.add(dodgeCard)
            println("$name dodge 1 attack by spending a Dodge card. Current cards: ${hand.size}.")
        } else {
            var attackCard: Card = Attack(null, null)
            for (card in hand) {
                if (card is Attack) {
                    println("[Dragon Heart] Zhao Yun's ATTACK and DODGE cards can be used interchangeably. (Attack for Dodge)")
                    attackCard = card
                    break
                }
            }
            hand.remove(attackCard)
            discardDeck.add(attackCard)
            println("$name dodge 1 attack by spending an ATTACK card. Current cards: ${hand.size}.")
        }
    }

    override fun spendDodgeCard() {
        var dodgeCard: Card = Dodge(null, null)
        var activateSkill = true
        for (card in hand) {
            if (card is Dodge) {
                dodgeCard = card
                activateSkill = false
            }
        }
        hand.remove(dodgeCard) //not yet prove
        discardDeck.add(dodgeCard)
        if(activateSkill == false){
            println("$name spend a DODGE card ${suitMap[dodgeCard.suit]} ${dodgeCard.rank} ${dodgeCard.name} to avoid a defect.")
        }

        var attackCard: Card = Attack(null, null)
        if (activateSkill == true) {
            for (card in hand) {
                if (card is Attack) {
                    println("[Dragon Heart] Zhao Yun's ATTACK and DODGE cards can be used interchangeably. (Attack for Dodge)")
                    attackCard = card
                }
            }
            hand.remove(attackCard)
            discardDeck.add(attackCard)
            println("$name spend an ATTACK card ${suitMap[dodgeCard.suit]} ${dodgeCard.rank} ${dodgeCard.name} to avoid a defect.")
        }
    }
}