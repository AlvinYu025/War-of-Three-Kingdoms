import Cards.*
import Factory.delone
import GeneralManager.discardDeck
import GeneralManager.generals
import Generals.*

abstract class Strategy(general: General) {
    open fun playNextCard() {
        attack()
    }

    open var rebelList: MutableList<General> = mutableListOf()
    open var commandCards: MutableList<CommandCard> = mutableListOf()
    open var lord: General? = null

    open var loyalistList: MutableList<General> = mutableListOf()

    abstract fun attack()
    abstract fun useCommandCard()

    abstract fun recovery()
    abstract fun checkPlayers()

    abstract fun horse()

    abstract fun weapon()

    abstract fun defense()


}

open class LoyalistStrategy(var general: General) : Strategy(general) {
    override var rebelList: MutableList<General> = mutableListOf()
    override var commandCards: MutableList<CommandCard> = mutableListOf()
    override var lord: General? = null

    override var loyalistList: MutableList<General> = mutableListOf()
    override fun recovery() {
        var numPeach = 0
        var handCopy = general.hand.toMutableList()
        for (card in general.hand) {
            if (card is Peach) {
                numPeach++
            }
        }
        if (general.currentHP < general.maxHP) {
            for (card in handCopy) {
                if (card is Peach) {
                    general.currentHP += 1
                    general.hand.remove(card)
                    discardDeck.add(card)
                    numPeach--
                }
            }
        }
        if (numPeach > 0) {
            for (general1 in generals) {
                var handCopy1 = general.hand.toMutableList()
                if (general1 != this.general) {
                    if (!general1.alive && (general1.player is Loyalist || general1.player is Lord) && numPeach > 0) {
                        for (card in handCopy1) {
                            if (card is Peach && !general.sun) {
                                this.general.hand.remove(card)
                                general1.currentHP++
                                discardDeck.add(card)
                                numPeach--
                                general1.alive = true
                                general1.killer = null
                                println("${this.general.name} uses a Peach card to save ${general1.name}")
                                break
                            } else if (card is Peach && general1.sun) {
                                if (this.general is WuGeneral) {
                                    this.general.hand.remove(card)
                                    general1.currentHP += 2
                                    discardDeck.add(card)
                                    numPeach--
                                    general1.alive = true
                                    general1.killer = null
                                    println("[Rescue] ${this.general.name} uses a Peach card to save ${general1.name} and rescue 2 hp")
                                    break
                                } else {
                                    this.general.hand.remove(card)
                                    general1.currentHP += 1
                                    discardDeck.add(card)
                                    numPeach--
                                    general1.alive = true
                                    general1.killer = null
                                    println("${this.general.name} uses a Peach card to save ${general1.name}")
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    override fun horse() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is HorseCard) {
                if (!general.hashouseone && card.name.compareTo("Shadowrunner") == 0) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    general.hand.remove(card)
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    break
                }
            }
        }
        val hand2 = general.hand.toMutableList()
        var num = 0
        for (card in hand2) {
            for (card1 in delone) {
                if (num < 3 && card.name.compareTo(card1) == 0) {
                    general.equipment.add(card)
                    general.hand.remove(card)
                    num++
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    break
                }
            }
        }
    }

    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel && gen.alive) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist && gen.alive) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true || gen.strategy is RebelStrategy) {
                    if (gen.alive) {
                        rebelList.add(gen)
                    }
                } else {
                    if (gen.alive) {
                        loyalistList.add(gen)
                    }
                }
            }
        }
    }


    override fun attack() {

        if (general.hasAttackCard()) {

            var tar = generals.random()
            while (tar.equals(general) || !tar.alive) {
                tar = generals.random()
            }
            if (rebelList.size != 0) tar = rebelList.random()

            if (general.player is Spy && (general.player as Spy).isRevealed == true) {
                tar = lord!!
            }
//      ***************************************************************************************************** SET ATTACK TIMES
            var attackingNum = 0                                                // default
            if (general.weapon is Zhuge_Crossbow) {                             // Zhuge Crossbow
                (general.weapon as Zhuge_Crossbow).execute(general, null)
                attackingNum =
                    general.hand.size                                // Zhuge Crossbow: maximum number of attacks = cards in hand
            } else {
                attackingNum = 1
            }
            if (general is ZhangFei) {
                println("[Berserk] Zhang Fei can use as many ATTACK cards as he wishes during the turn.")
                attackingNum =
                    general.hand.size                                // Zhang Fei's Skill: maximum number of attacks = cards in hand
            }
//      ***************************************************************************************************** SET ATTACK TIMES

            while (attackingNum > 0) {
                var attackCard = Attack(null, null)
                var can = false
                for (card in general.hand) {
                    if (card is Attack) {
                        checkRange(general)

                        if (general.attackRange >= checkDistance(general, tar)) {
                            attackCard = card
                            attackCard.receiver = tar
                            can = true
                            println("${general.name} can attack target general ${tar.name}.")
                            break
                        }
                    }
                }
                if (can) {
//      ***************************************************************************************************** BEFORE ATTACK
                    if (general.weapon is Yin_Yang_Swords) {                             // Yin-Yang Swords
                        (general.weapon as Yin_Yang_Swords).execute(general, tar)
                    } else if (general.weapon is Blue_Steel_Blade) {                     // Blue Steel Blade
                        (general.weapon as Blue_Steel_Blade).execute(general, tar)
                    }
//      ***************************************************************************************************** BEFORE ATTACK


//      ----------------------------------------------------------------------------------------------------- DURING ATTACK
                    println(
                        "${general.name} spends a card ${general.suitMap[attackCard.suit]} " + "${attackCard.rank} ${attackCard.name} to attack ${tar.name}."
                    )
                    val originalTarHP = tar.currentHP
                    tar.beingAttacked(general, attackCard.suit!!)
//      ----------------------------------------------------------------------------------------------------- DURING ATTACK


//      ##################################################################################################### AFTER ATTACK
                    if (tar.defense != null) {
                        tar.defense!!.valid =
                            true                                  // Blue Steel Blade: reset the defense card
                    }

                    val currentTarHP = tar.currentHP

                    if (originalTarHP > currentTarHP) {                               // Kirin Bow
                        if (general.weapon is Kirin_Bow) {
                            (general.weapon as Kirin_Bow).execute(general, tar)
                        }
                    } else {
                        if (general.weapon is Rock_Cleaving_Axe) {                      // Rock Cleaving Axe
                            if (tar.currentHP > 0) (general.weapon as Rock_Cleaving_Axe).execute(general, tar)
                        } else if (general.weapon is Green_Dragon_Blade) {              // Green Dragon Blade
                            (general.weapon as Green_Dragon_Blade).execute(general, null)
                            attackingNum++
                        }
                    }
                    general.hand.remove(attackCard)
                    discardDeck.add(attackCard)
                    attackingNum--                                      // update attack times
//      ##################################################################################################### AFTER ATTACK

                    if (tar.currentHP <= 0) {
                        break
                    }
                } else {
                    if (general.attackRange < checkDistance(general, tar)) {
                        print("(Distance) ")
                    }
                    println("${general.name} cannot attack target general ${tar.name}.")
                    break
                }
            }

        } else {
            if ((general.weapon is Serpent_Spear) && general.hand.size >= 4) {
                val tar = rebelList.random()
                (general.weapon as Serpent_Spear).execute(general, tar)
            } else {
                println("${general.name} doesn't have an attack card.")
            }
        }
    }

    override fun useCommandCard() {

        for (card in general.hand) {
            if (card is CommandCard) {
                commandCards.add(card)
            }
        }
        if (commandCards.size == 0) return

        if (rebelList.size == 0 && loyalistList.size == 0) return

        for (command in commandCards) {
            if (command.type.equals("good")) {
                command.initializer = general
                if (command.initializer!!.player is Loyalist) command.receiver = lord
                else {
                    command.receiver = lord
                    if (loyalistList.size != 0) command.receiver = loyalistList.random()
                }
                if(command.receiver!!.alive == false)
                    return

                command.execute()

            } else if (command.type.equals("judgement")) {
                command.initializer = general
                if (rebelList.size == 0) return

                while (command.receiver == null) {
                    command.receiver = rebelList.random()
                    break
                }
                command.initializer!!.hand.remove(command)
                command.receiver?.setJudgementCommand(command)
                println("${command.initializer!!.name} set a judgement ${command.name} to ${command.receiver?.name}")


            } else if (command.type.equals("bad")) {
                if (rebelList.size == 0) return
                command.initializer = general


                while (command.receiver == null || command.initializer == command.receiver) {
                    command.receiver = rebelList.random()
                    break
                }
                if(command.receiver!!.alive == false)
                    return

                var canNegate: Negate? = null
                var negator: General? = null
                for (rebel in rebelList) {
                    if (rebel.hasNegate() != null) {
                        canNegate = rebel.hasNegate()!!
                        negator = rebel
                    }
                }
                if (canNegate != null) {
                    canNegate.initializer = negator
                    canNegate.receiver = general
                    canNegate.commandCard = command
                    canNegate.execute()
                }
                command.execute()

            } else {
                if (lord?.currentHP!! >= 3 || lord?.hasDodgeCard() == true) {
                    command.initializer = general
                    command.receiver = generals.random()
                    command.execute()

                }
            }
        }
    }

    fun spyReveal() {

        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if ((general.player as Spy).isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }

        }
    }

    override fun playNextCard() {
        checkPlayers()
        spyReveal()
        checkPlayers()



        horse()
        weapon()
        defense()

        recovery()
        useCommandCard()
        attack()


    }
}

class RebelStrategy(val general: General) : Strategy(general) {
    override fun horse() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is HorseCard) {
                if (!general.hashouseone && card.name.compareTo("Shadowrunner") == 0) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    general.hand.remove(card)
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    break
                }
            }
        }
        val hand2 = general.hand.toMutableList()
        var num = 0
        for (card in hand2) {
            for (card1 in delone) {
                if (num < 3 && card.name.compareTo(card1) == 0) {
                    general.equipment.add(card)
                    general.hand.remove(card)
                    num++
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    break
                }
            }
        }

    }

    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    override fun recovery() {
        var numPeach = 0
        var handCopy = general.hand.toMutableList()
        for (card in general.hand) {
            if (card is Peach) {
                numPeach++
            }
        }
        if (general.currentHP < general.maxHP) {
            if (general.currentHP in 0..1) {
                for (card in handCopy) {
                    if (card is Peach) {
                        general.currentHP += 1
                        general.hand.remove(card)
                        discardDeck.add(card)
                    }
                }
            }
        }
        if (numPeach > 0) {
            for (general in generals) {
                var handCopy1 = general.hand.toMutableList()
                if (general != this.general) {
                    if (!general.alive && numPeach > 0) {
                        if (general.player is Rebel || (general.player is Spy && general.player.isRevealed)) {
                            for (card in handCopy1) {
                                if (card is Peach) {
                                    this.general.hand.remove(card)
                                    general.currentHP++
                                    if (general.currentHP <= 0) general.currentHP = 1
                                    discardDeck.add(card)
                                    numPeach--
                                    general.alive = true
                                    general.killer = null
                                    println("${this.general.name} uses a Peach card to save ${general.name}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {

            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }

    }

    fun spyReveal() {
        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if (general.player.isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }
        }
    }

    override fun attack() {
        if (general.hasAttackCard()) {

            var tar: General? = null
            for (gen in generals) {
                if (gen.player is Lord) {
                    tar = gen
                }
            }
//      ***************************************************************************************************** SET ATTACK TIMES
            var attackingNum = 0                                                // default
            if (general.weapon is Zhuge_Crossbow) {                             // Zhuge Crossbow
                (general.weapon as Zhuge_Crossbow).execute(general, null)
                attackingNum =
                    general.hand.size                                // Zhuge Crossbow: maximum number of attacks = cards in hand
            } else {
                attackingNum = 1
            }
            if (general is ZhangFei) {
                println("[Berserk] Zhang Fei can use as many ATTACK cards as he wishes during the turn.")
                attackingNum =
                    general.hand.size                                // Zhang Fei's Skill: maximum number of attacks = cards in hand
            }
//      ***************************************************************************************************** SET ATTACK TIMES

            while (attackingNum > 0) {
                var attackCard = Attack(null, null)
                var can = false
                for (card in general.hand) {
                    if (card is Attack) {
                        checkRange(general)

                        if (general.attackRange >= checkDistance(general, tar!!)) {
                            attackCard = card
                            attackCard.receiver = tar
                            can = true
                            println("${general.name} can attack target general ${tar.name}.")
                            break
                        }
                    }
                }
                if (can) {
//      ***************************************************************************************************** BEFORE ATTACK
                    if (general.weapon is Yin_Yang_Swords) {                             // Yin-Yang Swords
                        (general.weapon as Yin_Yang_Swords).execute(general, tar)
                    } else if (general.weapon is Blue_Steel_Blade) {                     // Blue Steel Blade
                        (general.weapon as Blue_Steel_Blade).execute(general, tar)
                    }
//      ***************************************************************************************************** BEFORE ATTACK


//      ----------------------------------------------------------------------------------------------------- DURING ATTACK
                    println(
                        "${general.name} spends a card ${general.suitMap[attackCard.suit]} " + "${attackCard.rank} ${attackCard.name} to attack ${tar!!.name}."
                    )
                    val originalTarHP = tar.currentHP
                    tar.beingAttacked(general, attackCard.suit!!)
//      ----------------------------------------------------------------------------------------------------- DURING ATTACK


//      ##################################################################################################### AFTER ATTACK
                    if (tar.defense != null) {
                        tar.defense!!.valid =
                            true                                  // Blue Steel Blade: reset the defense card
                    }

                    val currentTarHP = tar.currentHP

                    if (originalTarHP > currentTarHP) {                               // Kirin Bow
                        if (general.weapon is Kirin_Bow) {
                            (general.weapon as Kirin_Bow).execute(general, tar)
                        }
                    } else {
                        if (general.weapon is Rock_Cleaving_Axe) {                      // Rock Cleaving Axe
                            if (tar.currentHP > 0) (general.weapon as Rock_Cleaving_Axe).execute(general, tar)
                        } else if (general.weapon is Green_Dragon_Blade) {              // Green Dragon Blade
                            (general.weapon as Green_Dragon_Blade).execute(general, null)
                            attackingNum++
                        }
                    }
                    general.hand.remove(attackCard)
                    discardDeck.add(attackCard)
                    attackingNum--                                      // update attack times
//      ##################################################################################################### AFTER ATTACK
                    if (tar is CaoCao) {
                        var helpCao = false
                        if (tar.nextGeneral != null) {
                            tar.nextGeneral!!.canHelpLord(general)
                            tar.canActivateEntourageList.forEach {
                                if (it) {
                                    helpCao = true
                                }
                            }
                        }
                        if (!helpCao) {
                            tar.treachery(attackCard)
                        }
                    }

                    if (tar.currentHP <= 0) {
                        break
                    }
                } else {
                    if (general.attackRange < checkDistance(general, tar!!)) {
                        print("(Distance) ")
                    }
                    println("${general.name} cannot attack target general ${tar.name}.")
                    break
                }
            }

        } else {
            if ((general.weapon is Serpent_Spear) && general.hand.size >= 4) {
                val tar = rebelList.random()
                (general.weapon as Serpent_Spear).execute(general, tar)
            } else {
                println("${general.name} doesn't have an attack card.")
            }
        }
    }

    override fun useCommandCard() {
        for (card in general.hand) {
            if (card is CommandCard) {
                commandCards.add(card)
            }
        }
        if (commandCards.size == 0) return
        for (command in commandCards) {
            if (command.type.equals("good")) {
                command.initializer = general

                command.receiver = rebelList.random()
                if(command.receiver!!.alive == false)
                    return
                command.execute()

            } else if (command.type.equals("judgement")) {
                command.initializer = general

                if (lord?.currentHP!! <= 3) {
                    command.receiver = lord
                } else {
                    command.receiver = lord
                    if (loyalistList.size != 0) command.receiver = loyalistList.random()
                }
                command.initializer!!.hand.remove(command)
                command.receiver!!.setJudgementCommand(command)
                println("${command.initializer!!.name} set a judgement ${command.name} to ${command.receiver?.name}")

            } else if (command.type.equals("bad")) {
                command.initializer = general


                if (lord?.currentHP!! <= 3) {
                    command.receiver = lord
                } else {
                    command.receiver = lord
                    if (loyalistList.size != 0) command.receiver = loyalistList.random()
                    if (command.initializer!!.equals(command.receiver)) return
                }
                if(command.receiver!!.alive == false)
                    return
                var canNegate: Negate? = null
                var negator: General? = null
                if (lord?.hasNegate() != null) {
                    canNegate = lord!!.hasNegate()!!
                    negator = lord
                }
                for (loyalist in loyalistList) {
                    if (loyalist.hasNegate() != null) {
                        canNegate = loyalist.hasNegate()!!
                        negator = loyalist
                    }
                }
                if (canNegate != null) {
                    canNegate.initializer = negator
                    canNegate.receiver = general
                    canNegate.commandCard = command
                    canNegate.execute()
                }

                command.execute()

            } else {
                if (lord?.hasDodgeCard() == false) {
                    command.initializer = general
                    command.receiver = generals.random()
                    if (loyalistList.size != 0) command.receiver = loyalistList.random()
                    command.execute()

                }
            }
        }
    }


    override fun playNextCard() {
        checkPlayers()
        spyReveal()
        checkPlayers()
        if (rebelList.size <= 1 && general.player is Spy) {
            (general.player as? Spy)?.isRevealed = true
        }
        if (general.player is Spy) {
            if (general.player.isRevealed == true) general.strategy = RebelStrategy(general)
        }
        if (general.currentHP < general.maxHP) {
            recovery()
        }
        horse()
        weapon()
        defense()
        recovery()
        useCommandCard()
        attack()
    }

}

class LiuBeiStrategy(general: General) : LoyalistStrategy(general) {
    lateinit var state: State

    override fun playNextCard() {
        checkPlayers()
        spyReveal()
        checkPlayers()
        state.playNextCard()
        super.horse()
        super.weapon()
        super.defense()

        super.recovery()
        super.useCommandCard()
        attack()
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
    }

    open fun initializeStrategy() {
        if (general.currentHP >= 2) {
            state = HealthyState(general.strategy as LiuBeiStrategy)
        } else {
            state = UnhealthyState(general.strategy as LiuBeiStrategy)
        }
    }

    override fun attack() {
        var tar: General? = null
        if (rebelList.size == 0) {
            return
        }
        tar = rebelList.random()

        if (general.hasAttackCard()) {
//      ***************************************************************************************************** SET ATTACK TIMES
            var attackingNum = 0                                                // default
            if (general.weapon is Zhuge_Crossbow) {                             // Zhuge Crossbow
                (general.weapon as Zhuge_Crossbow).execute(general, null)
                attackingNum =
                    general.hand.size                                // Zhuge Crossbow: maximum number of attacks = cards in hand
            } else {
                attackingNum = 1
            }
//      ***************************************************************************************************** SET ATTACK TIMES

            while (attackingNum > 0) {
                var attackCard: Card? = null
                var can = false
                for (card in general.hand) {
                    if (card is Attack) {
                        checkRange(general)

                        if (general.attackRange >= checkDistance(general, tar)) {
                            attackCard = card
                            attackCard.receiver = tar
                            can = true
                            println("Liu Bei can attack general ${tar.name}.")
                            break
                        }
                    }
                }
                if (can) {
//      ***************************************************************************************************** BEFORE ATTACK
                    if (general.weapon is Yin_Yang_Swords) {                             // Yin-Yang Swords
                        (general.weapon as Yin_Yang_Swords).execute(general, tar)
                    } else if (general.weapon is Blue_Steel_Blade) {                     // Blue Steel Blade
                        (general.weapon as Blue_Steel_Blade).execute(general, tar)
                    }
//      ***************************************************************************************************** BEFORE ATTACK


                    val rouse = (general as LiuBei).RouseJudgement()
//      ----------------------------------------------------------------------------------------------------- DURING ATTACK
                    if (rouse == false) {
                        println(
                            "${general.name} spends a card ${general.suitMap[attackCard!!.suit]} " + "${attackCard.rank} ${attackCard.name} to attack ${tar.name}."
                        )

                        val originalTarHP = tar.currentHP
                        tar.beingAttacked(general, attackCard.suit!!)
//      ----------------------------------------------------------------------------------------------------- DURING ATTACK

//      ##################################################################################################### AFTER ATTACK
                        if (tar.defense != null) {
                            tar.defense!!.valid =
                                true                                  // Blue Steel Blade: reset the defense card
                        }

                        val currentTarHP = tar.currentHP

                        if (originalTarHP > currentTarHP) {                               // Kirin Bow
                            if (general.weapon is Kirin_Bow) {
                                (general.weapon as Kirin_Bow).execute(general, tar)
                            }
                        } else {
                            if (general.weapon is Rock_Cleaving_Axe) {                      // Rock Cleaving Axe
                                if (tar.currentHP > 0) (general.weapon as Rock_Cleaving_Axe).execute(general, tar)
                            } else if (general.weapon is Green_Dragon_Blade) {              // Green Dragon Blade
                                (general.weapon as Green_Dragon_Blade).execute(general, null)
                                attackingNum++
                            }
                        }
                        general.hand.remove(attackCard)
                        discardDeck.add(attackCard)
                        attackingNum--                                      // update attack times
//      ##################################################################################################### AFTER ATTACK
                        if (tar is CaoCao && lord is CaoCao) {
                            var helpCao = false
                            if (tar.nextGeneral != null) {
                                tar.nextGeneral!!.canHelpLord(general)
                                tar.canActivateEntourageList.forEach {
                                    if (it) {
                                        helpCao = true
                                    }
                                }
                            }
                            if (!helpCao) {
                                tar.treachery(attackCard)
                            }
                        }
                    } else {
                        attackCard = (general as LiuBei).RouseAttack()
                        println(
                            "${general.name} spends a card ${general.suitMap[attackCard!!.suit]} " + "${attackCard.rank} ${attackCard.name} to attack ${tar.name}."
                        )

//      ----------------------------------------------------------------------------------------------------- DURING ATTACK
                        val originalTarHP = tar.currentHP
                        tar.beingAttacked(general, attackCard.suit!!)

//      ##################################################################################################### AFTER ATTACK
                        val currentTarHP = tar.currentHP

                        if (tar.defense != null) {
                            tar.defense!!.valid =
                                true                                  // Blue Steel Blade: reset the defense card
                        }

                        if (originalTarHP > currentTarHP) {                               // Kirin Bow
                            if (general.weapon is Kirin_Bow) {
                                (general.weapon as Kirin_Bow).execute(general, tar)
                            }
                        } else {
                            if (general.weapon is Rock_Cleaving_Axe) {                      // Rock Cleaving Axe
                                if (tar.currentHP > 0) (general.weapon as Rock_Cleaving_Axe).execute(general, tar)
                            } else if (general.weapon is Green_Dragon_Blade) {              // Green Dragon Blade
                                (general.weapon as Green_Dragon_Blade).execute(general, null)
                                attackingNum++
                            }
                        }

                        attackingNum--                                      // update attack times
//      ##################################################################################################### AFTER ATTACK
                        if (tar is CaoCao && lord is CaoCao) {
                            var helpCao = false
                            if (tar.nextGeneral != null) {
                                tar.nextGeneral!!.canHelpLord(general)
                                tar.canActivateEntourageList.forEach {
                                    if (it) {
                                        helpCao = true
                                    }
                                }
                            }
                            if (!helpCao) {
                                tar.treachery(attackCard)
                            }
                        }
                    }

                    if (tar.currentHP <= 0) {
                        break
                    }
                } else {
                    if (general.attackRange < checkDistance(general, tar)) {
                        print("(Distance) ")
                    }
                    println("Liu Bei cannot attack general ${tar.name}.")
                    break
                }
            }

        } else {
            if ((general.weapon is Serpent_Spear) && general.hand.size >= 4) {
                val tar = rebelList.random()
                (general.weapon as Serpent_Spear).execute(general, tar)
            } else {
                println("${general.name} doesn't have an attack card.")
            }
        }
    }
}

class SunQuanStrategy(val general: General) : Strategy(general) {
    open lateinit var identityStrategy: Strategy

    override fun horse() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is HorseCard) {
                if (card.name.compareTo("Shadowrunner Spade 5") == 0 && !general.hashouseone) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    general.hand.remove(card)
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                }
                for (card1 in delone) {
                    if (card.name.compareTo(card1) == 0 && !general.delhouseone) {
                        general.equipment.add(card)
                        general.hashouseone = true
                        general.hand.remove(card)
                        println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    }
                }
            }
        }
    }

    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    override fun recovery() {
        var numPeach = 0
        var handCopy = general.hand.toMutableList()
        for (card in general.hand) {
            if (card is Peach) {
                numPeach++
            }
        }
        if (general.currentHP < general.maxHP) {
//            if (general.currentHP in 0..1) {
            for (card in handCopy) {
                if (card is Peach) {
                    general.currentHP += 1
                    general.hand.remove(card)
                    discardDeck.add(card)
                    numPeach--
                }
            }
//            }
        }
        if (numPeach > 0) {
            for (general in generals) {
                var handCopy1 = general.hand.toMutableList()
                if (general != this.general) {
                    if (!general.alive && (general.player is Loyalist || general.player is Lord) && numPeach > 0) {
                        for (card in handCopy1) {
                            if (card is Peach) {
                                this.general.hand.remove(card)
                                general.currentHP++
                                discardDeck.add(card)
                                numPeach--
                                general.alive = true
                                general.killer = null
                                println("${this.general.name} uses a Peach card to save ${general.name}")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy && (gen.player as? Spy)?.isRevealed == true) {
                if (gen.strategy is RebelStrategy) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }

    }

    override fun attack() {
        identityStrategy.attack()
    }

    override fun useCommandCard() {
        identityStrategy.useCommandCard()
    }

    fun Rebalancing() {
        var l: MutableList<Int> = mutableListOf()
        for (i in 1 until general.hand.size) {
            l.add(i)
        }

        var noneed = l.random()
        general.discard(noneed)
        general.draw(noneed)
        var na = general.name
        println("[Rebalancing] $na using skill to rebalance cards")
    }

    fun spyReveal() {

        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if (general.player.isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }

        }
    }

    override fun playNextCard() {
        Rebalancing()
        checkPlayers()
        spyReveal()
        checkPlayers()
        identityStrategy.checkPlayers()
        recovery()
        identityStrategy.horse()
        identityStrategy.weapon()
        identityStrategy.defense()
        identityStrategy.useCommandCard()
        identityStrategy.attack()
    }
}

class HuatuoStrategy(val general: General) : Strategy(general) {
    open lateinit var identityStrategy: Strategy
    var nhas = false

    fun spyReveal() {

        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if (general.player.isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }

        }
    }

    override fun horse() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is HorseCard) {
                if (card.name.compareTo("Shadowrunner Spade 5") == 0 && !general.hashouseone) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    general.hand.remove(card)
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                }
                for (card1 in delone) {
                    if (card.name.compareTo(card1) == 0 && !general.delhouseone) {
                        general.equipment.add(card)
                        general.hashouseone = true
                        general.hand.remove(card)
                        println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    }
                }
            }
        }
    }


    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    override fun recovery() {
        var numPeach = 0
        var handCopy = general.hand.toMutableList()
        for (card in general.hand) {
            if (card is Peach) {
                numPeach++
            }
        }
        if (general.currentHP < general.maxHP) {
            for (card in handCopy) {
                if (card is Peach) {
                    general.currentHP += 1
                    general.hand.remove(card)
                    discardDeck.add(card)
                    numPeach--
                    nhas = true
                    break
                }
            }
            if (!nhas) {
                val hand2 = general.hand.toMutableList()
                for (card in hand2) {
                    if (card.suit!!.compareTo("Diamond") == 0 || card.suit!!.compareTo("Heart") == 0) {
                        general.currentHP += 1
                        general.hand.remove(card)
                        discardDeck.add(card)
                        nhas = true
                        println("[First Aid] ${general.name} using a black card to recover")
                        break
                    }
                }
//                }
            }
        }
        if (numPeach > 0) {
            for (general1 in generals) {
                val hand3 = general.hand.toMutableList()
                if (general1 != this.general) {
                    if (this.identityStrategy is LoyalistStrategy && !general1.alive && (general1.player is Loyalist || general.player is Lord) && numPeach > 0) {
                        for (card in hand3) {
                            if (card is Peach && !general.sun) {
                                general.hand.remove(card)
                                general1.currentHP++
                                discardDeck.add(card)
                                numPeach--
                                general1.alive = true
                                general1.killer = null
                                println("${this.general.name} uses a Peach card to save ${general1.name}")
                                break
                            } else if (card is Peach && general1.sun) {
                                if (this.general is WuGeneral) {
                                    general.hand.remove(card)
                                    general1.currentHP += 2
                                    discardDeck.add(card)
                                    numPeach--
                                    general1.alive = true
                                    general1.killer = null
                                    println("[Rescue] ${this.general.name} uses a Peach card to save ${general1.name} and rescue 2 hp")
                                    break
                                } else {
                                    general.hand.remove(card)
                                    general1.currentHP += 1
                                    discardDeck.add(card)
                                    numPeach--
                                    general1.alive = true
                                    general1.killer = null
                                    println("${this.general.name} uses a Peach card to save ${general1.name}")
                                    break
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (gen in generals) {
                val hand4 = general.hand.toMutableList()
                for (card in hand4) {
                    if ((card.suit!!.compareTo("Diamond") == 0 || card.suit!!.compareTo("Heart") == 0)) {
                        if (gen != this.general) {
                            if (this.identityStrategy is LoyalistStrategy && !gen.alive && (general.player is Loyalist || general.player is Lord)) {
                                this.general.hand.remove(card)
                                gen.currentHP++
                                discardDeck.add(card)
                                numPeach--
                                gen.alive = true
                                gen.killer = null
                                println("[First Aid] ${this.general.name} uses a red suit card to save ${gen.name}")
                                break
                            } else if (gen.player is Rebel || (gen.player is Spy && gen.player.isRevealed)) {
                                this.general.hand.remove(card)
                                gen.currentHP++
                                discardDeck.add(card)
                                numPeach--
                                gen.alive = true
                                gen.killer = null
                                println("[First Aid] ${this.general.name} uses a red suit card to save ${gen.name}")
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    fun Medical() {
        if (identityStrategy is RebelStrategy) {
            for (gen in rebelList) {
                if (gen != general && gen.currentHP < gen.maxHP) {
                    if (general.hand.size > 0) {
                        general.discard(1)
                        gen.currentHP += 1
                        println("[Medical] ${this.general.name} uses a red suit card to save ${gen.name}")
                        break
                    }
                }
            }
        } else if (identityStrategy is LoyalistStrategy) {
            for (gen in loyalistList) {
                if (gen != general && gen.currentHP < gen.maxHP) {
                    if (general.hand.size > 0) {
                        general.discard(1)
                        gen.currentHP += 1
                        println("[Medical] ${this.general.name} uses a red suit card to save ${gen.name}")
                        break
                    }
                }
            }
        }

    }

    override fun playNextCard() {
        checkPlayers()
        spyReveal()
        checkPlayers()
        identityStrategy.checkPlayers()
        recovery()
        Medical()
        identityStrategy.weapon()
        identityStrategy.defense()
        identityStrategy.horse()
        identityStrategy.useCommandCard()
        identityStrategy.attack()

    }

    override fun attack() {
        identityStrategy.attack()
    }

    override fun useCommandCard() {
        identityStrategy.useCommandCard()
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true || gen.strategy is RebelStrategy) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }

    }

}

class DiaoChanStrategy(val general: General) : Strategy(general) {

    override fun horse() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is HorseCard) {
                if (card.name.compareTo("Shadowrunner") == 0 && !general.hashouseone) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    general.hand.remove(card)
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                }
            }
        }
        val hand2 = general.hand.toMutableList()
        for (card in hand2) {
            for (card1 in delone) {
                if (card.name.compareTo(card1) == 0 && !general.delhouseone) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    general.hand.remove(card)
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                }
            }
        }

    }

    fun spyReveal() {

        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if (general.player.isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }

        }
    }

    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    open lateinit var identityStrategy: Strategy
    override fun recovery() {
        var hand1 = general.hand.toMutableList()
        if (general.currentHP in 0..1) {
            for (card in hand1) {
                if (card is Peach) {
                    general.currentHP += 1
                    general.hand.remove(card)
                    discardDeck.add(card)
                }
            }
        }
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true || gen.strategy is RebelStrategy) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }

    }


    override fun attack() {
        identityStrategy.attack()
    }

    override fun useCommandCard() {
        identityStrategy.useCommandCard()
    }

    fun Du(re: General?) {
        if (re?.hasAttackCard() == false) {
            re.currentHP--
            var name = re.name
            println("[Alienation] $name has been attacked by Diaochan's skill")
        } else {
            re!!.spendAttackCard()
            var name = re.name

            println("[Alienation] $name has spend card to avoid attack from Diaochan's skill")
        }
    }

    var l: MutableList<Boolean> = mutableListOf(true, false)
    fun Alienation() {
        var doit = l.random()
        if (doit) {
            general.hand.remove(general.hand.random())

            var duel: MutableList<General> = mutableListOf()
            if (identityStrategy is LoyalistStrategy) {
                for (gen in rebelList) {
                    if (gen.gender.compareTo("Male") == 0) {
                        duel.add(gen)
                    }
                }
                if (duel.size >= 2) {
                    for (i in 0..1) {
                        Du( duel[i])
                    }
                }
            } else if (identityStrategy is RebelStrategy) {
                for (gen in loyalistList) {
                    if (gen.gender.compareTo("Male") == 0) {
                        duel.add(gen)
                    }
                }
                if (duel.size >= 2) {
                    for (i in 0..1) {
                        Du(duel[i])
                    }
                }
            }
            duel.clear()
        }


    }

    override fun playNextCard() {
        checkPlayers()
        spyReveal()
        checkPlayers()
        identityStrategy.checkPlayers()
        Alienation()
        identityStrategy.playNextCard()


    }
}

class SunShangXiangStrategy(val general: General) : Strategy(general) {
    open lateinit var identityStrategy: Strategy

    override fun horse() {
        for (card in general.hand) {
            if (card is HorseCard) {
                if (card.name.compareTo("Shadowrunner Spade 5") == 0 && !general.hashouseone) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    general.hand.remove(card)
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                }
                for (card1 in delone) {
                    if (card.name.compareTo(card1) == 0 && !general.delhouseone) {
                        general.equipment.add(card)
                        general.hashouseone = true
                        general.hand.remove(card)
                        println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    }
                }
            }
        }
    }

    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    fun spyReveal() {

        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if (general.player.isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }

        }
    }

    override fun recovery() {
        var hand1 = general.hand.toMutableList()
        if (general.currentHP in 0..1) {
            for (card in hand1) {
                if (card is Peach) {
                    general.currentHP += 1
                    general.hand.remove(card)
                    discardDeck.add(card)
                }
            }
        }
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true || gen.strategy is RebelStrategy) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }

    }


    override fun attack() {
        identityStrategy.attack()

    }

    override fun useCommandCard() {
        identityStrategy.checkPlayers()
        identityStrategy.useCommandCard()

    }

    fun betrothment(target: General) {
        if (general.hand.size < 2 || general.currentHP == general.maxHP) {// only work when have more than 2 cards
            return
        }
        for (i in 1..2) {
            val random = general.hand.random()
            general.hand.remove(random)
            discardDeck.add(random)
        }
        general.currentHP++
        target.currentHP++
        if (target.currentHP > target.maxHP) {
            target.currentHP = target.maxHP
        }
        println(
            "[Betrothment] ${general.name} discard two cards, ${general.name} and ${target.name} recover 1 health point," + " current HP are ${general.currentHP} and ${target.currentHP}"
        )
    }

    fun getTargetGeneral(): General? {
        if (identityStrategy is LoyalistStrategy) {
            return lord
        } else if (identityStrategy is RebelStrategy) {
            if (rebelList.size == 0) return null
            var gen = rebelList.random()
            while (gen.equals(general)) {
                gen = rebelList.random()
                break
            }
            return gen
        }
        return null


    }

    override fun playNextCard() {
        checkPlayers()
        spyReveal()
        checkPlayers()
        identityStrategy.checkPlayers()
        if (general.hand.size >= 2 && general.currentHP < general.maxHP) {
            var target = getTargetGeneral()
            if (target != null) {
                betrothment(target)
            }
        }

        identityStrategy.playNextCard()


    }

}

class LvMengStrategy(val general: General) : Strategy(general) {

    open lateinit var identityStrategy: Strategy


    override fun horse() {
        for (card in general.hand) {
            if (card is HorseCard) {
                if (card.name.compareTo("Shadowrunner Spade 5") == 0 && !general.hashouseone) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                }
                for (card1 in delone) {
                    if (card.name.compareTo(card1) == 0 && !general.delhouseone) {
                        general.equipment.add(card)
                        general.hashouseone = true
                        println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    }
                }
            }
        }
    }

    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    fun spyReveal() {

        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if (general.player.isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }

        }
    }

    override fun recovery() {
        var hand1 = general.hand.toMutableList()
        if (general.currentHP in 0..1) {
            for (card in hand1) {
                if (card is Peach) {
                    general.currentHP += 1
                    general.hand.remove(card)
                    discardDeck.add(card)
                }
            }
        }
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true || gen.strategy is RebelStrategy) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }

    }

    override fun attack() {
        identityStrategy.attack()
        (general as? LvMeng)!!.hasSpentAttack = true
    }

    override fun useCommandCard() {

        identityStrategy.useCommandCard()

    }

    override fun playNextCard() {

        checkPlayers()
        spyReveal()
        checkPlayers()
        identityStrategy.checkPlayers()
        identityStrategy.horse()
        weapon()
        if (general.currentHP < general.maxHP) identityStrategy.recovery()

        identityStrategy.useCommandCard()

        if (general.hand.size >= 6)//only attack when the number of card to discard is not much
            attack()


    }

}


class HuangGaiStrategy(val general: General) : Strategy(general) {

    open lateinit var identityStrategy: Strategy


    override fun horse() {
        for (card in general.hand) {
            if (card is HorseCard) {
                if (card.name.compareTo("Shadowrunner Spade 5") == 0 && !general.hashouseone) {
                    general.equipment.add(card)
                    general.hashouseone = true
                    println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                }
                for (card1 in delone) {
                    if (card.name.compareTo(card1) == 0 && !general.delhouseone) {
                        general.equipment.add(card)
                        general.hashouseone = true
                        println("${general.name} takes the horse: [${card.name}] from his/her hand to the horse area.")
                    }
                }
            }
        }
    }

    override fun weapon() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is WeaponCard && !general.hasweapon) {
                general.weapon = card
                println("${general.name} takes the weapon: [${card.name}] from his/her hand to the weapon area.")
                general.hasweapon = true
                general.hand.remove(card)
            }
        }
    }

    override fun defense() {
        val hand1 = general.hand.toMutableList()
        for (card in hand1) {
            if (card is DefenseCard && !general.hasdefense) {
                general.defense = card
                println("${general.name} takes the defense: [${card.name}] from his/her hand to the defense area.")
                general.hasdefense = true
                general.hand.remove(card)
            }
        }
    }

    fun spyReveal() {

        if (general.player is Spy) {
            if (rebelList.size == 0) (general as? Spy)?.isRevealed = true
            if (general.player.isRevealed == true) {
                general.strategy = RebelStrategy(general)
                if (general is SunShangXiang) (general as? SunShangXiang)?.initializeIdentityStrategy()
                if (general is HuangGai) (general as? HuangGai)?.initializeIdentityStrategy()
                if (general is LvMeng) (general as? LvMeng)?.initializeIdentityStrategy()
                if (general is Huatuo) (general as? Huatuo)?.initializeIdentityStrategy()
                if (general is SunQuan) (general as? SunQuan)?.initializeIdentityStrategy()
                if (general is DiaoChan) (general as? DiaoChan)?.initializeIdentityStrategy()
            }

        }
    }

    override fun recovery() {
        var hand1 = general.hand.toMutableList()
        if (general.currentHP in 0..1) {
            for (card in hand1) {
                if (card is Peach) {
                    general.currentHP += 1
                    general.hand.remove(card)
                    discardDeck.add(card)
                }
            }
        }
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true || gen.strategy is RebelStrategy) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }

    }

    override fun attack() {
        identityStrategy.attack()
        (general as? LvMeng)!!.hasSpentAttack = true
    }

    override fun useCommandCard() {
        identityStrategy.useCommandCard()
    }

    override fun playNextCard() {

        checkPlayers()
        spyReveal()
        checkPlayers()
        identityStrategy.checkPlayers()
        identityStrategy.horse()
        weapon()
        defense()

        if (general.currentHP >= 2 && general.hand.size < 3) (general as? HuangGai)?.sacrifice()
        if (general.currentHP < general.maxHP) identityStrategy.recovery()

        identityStrategy.useCommandCard()

        identityStrategy.attack()
    }
}

class ZhaoYunStrategy(val general: General) : Strategy(general) {
    open lateinit var identityStrategy: Strategy
    override fun attack() {
        var tar: General? = null
        if (general.player is Loyalist || (general.player is Spy && !general.player.isRevealed)) {
            if (rebelList.size == 0) {
                return
            }
            tar = rebelList.random()
        } else {
            tar = lord
        }
        if (general.hasAttackCard()) {
//      ***************************************************************************************************** SET ATTACK TIMES
            var attackingNum = 0                                                // default
            if (general.weapon is Zhuge_Crossbow) {                             // Zhuge Crossbow
                (general.weapon as Zhuge_Crossbow).execute(general, null)
                attackingNum =
                    general.hand.size                                // Zhuge Crossbow: maximum number of attacks = cards in hand
            } else {
                attackingNum = 1
            }
//      ***************************************************************************************************** SET ATTACK TIMES

            while (attackingNum > 0) {
                var attackCard: Card? = null
                var can = false
                for (card in general.hand) {
                    if (card is Attack) {
                        checkRange(general)

                        if (general.attackRange >= checkDistance(general, tar!!)) {
                            attackCard = card
                            attackCard.receiver = tar
                            can = true
                            println("Zhao Yun can attack general ${tar.name}.")
                            break
                        }
                    }
                    if (card is Dodge) {
                        checkRange(general)

                        if (general.attackRange >= checkDistance(general, tar!!)) {
                            attackCard = card
                            attackCard.receiver = tar
                            can = true
                            println("Zhao Yun can attack general ${tar.name}.")
                            break
                        }
                    }
                }
                if (can) {
//      ***************************************************************************************************** BEFORE ATTACK
                    if (general.weapon is Yin_Yang_Swords) {                             // Yin-Yang Swords
                        (general.weapon as Yin_Yang_Swords).execute(general, tar)
                    } else if (general.weapon is Blue_Steel_Blade) {                     // Blue Steel Blade
                        (general.weapon as Blue_Steel_Blade).execute(general, tar)
                    }
//      ***************************************************************************************************** BEFORE ATTACK


//      ----------------------------------------------------------------------------------------------------- DURING ATTACK
                    println(
                        "${general.name} spends a card ${general.suitMap[attackCard!!.suit]} " + "${attackCard.rank} ${attackCard.name} to attack ${tar!!.name}."
                    )
                    val originalTarHP = tar.currentHP
                    tar.beingAttacked(general, attackCard.suit!!)
//      ----------------------------------------------------------------------------------------------------- DURING ATTACK


//      ##################################################################################################### AFTER ATTACK
                    if (tar.defense != null) {
                        tar.defense!!.valid =
                            true                                  // Blue Steel Blade: reset the defense card
                    }

                    val currentTarHP = tar.currentHP

                    if (originalTarHP > currentTarHP) {                               // Kirin Bow
                        if (general.weapon is Kirin_Bow) {
                            (general.weapon as Kirin_Bow).execute(general, tar)
                        }
                    } else {
                        if (general.weapon is Rock_Cleaving_Axe) {                      // Rock Cleaving Axe
                            if (tar.currentHP > 0) (general.weapon as Rock_Cleaving_Axe).execute(general, tar)
                        } else if (general.weapon is Green_Dragon_Blade) {              // Green Dragon Blade
                            (general.weapon as Green_Dragon_Blade).execute(general, null)
                            attackingNum++
                        }
                    }
                    general.hand.remove(attackCard)
                    discardDeck.add(attackCard)
                    attackingNum--                                      // update attack times
//      ##################################################################################################### AFTER ATTACK
                    if (tar is CaoCao && lord is CaoCao) {
                        var helpCao = false
                        if (tar.nextGeneral != null) {
                            tar.nextGeneral!!.canHelpLord(general)
                            tar.canActivateEntourageList.forEach {
                                if (it) {
                                    helpCao = true
                                }
                            }
                        }
                        if (!helpCao) {
                            tar.treachery(attackCard)
                        }
                    }
                    if (tar.currentHP <= 0) {
                        break
                    }
                } else {
                    if (general.attackRange < checkDistance(general, tar!!)) {
                        print("(Distance) ")
                    }
                    println("Zhao Yun cannot attack general ${tar.name}.")
                    break
                }
            }

        } else {
            if ((general.weapon is Serpent_Spear) && general.hand.size >= 4) {
                val tar = rebelList.random()
                (general.weapon as Serpent_Spear).execute(general, tar)
            } else {
                println("${general.name} doesn't have an attack card.")
            }
        }
    }

    override fun useCommandCard() {
        identityStrategy.useCommandCard()
    }

    override fun recovery() {
        identityStrategy.recovery()
    }

    override fun checkPlayers() {
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
        for (gen in generals) {
            if (gen.player is Lord) {
                lord = gen
            }
        }
        for (gen in generals) {
            if (gen.player is Rebel) {
                rebelList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Loyalist) {
                loyalistList.add(gen)
            }
        }
        for (gen in generals) {
            if (gen.player is Spy) {
                if (rebelList.size == 0) {
                    gen.player.isRevealed = true
                }
                if ((gen.player as? Spy)?.isRevealed == true || gen.strategy is RebelStrategy) {
                    rebelList.add(gen)
                } else {
                    loyalistList.add(gen)
                }
            }
        }
    }

    override fun horse() {
        identityStrategy.horse()
    }

    override fun weapon() {
        identityStrategy.weapon()
    }

    override fun defense() {
        identityStrategy.weapon()
    }

    override fun playNextCard() {
        checkPlayers()
        identityStrategy.checkPlayers()

        identityStrategy.horse()
        weapon()
        defense()

        identityStrategy.recovery()
        useCommandCard()
        attack()
        commandCards.clear()
        loyalistList.clear()
        rebelList.clear()
    }
}