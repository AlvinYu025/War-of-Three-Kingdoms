package Factory

import Cards.*
import Cards.Attack
import Cards.Card
import Cards.Dodge
import Cards.Peach
import Cards.Eight_Trigrams_Formation

val delone: MutableList<String> = mutableListOf(
    "Ferghana Horse", "Violet Stallion", "Red Hare"
)

val HorseCardList: MutableList<String> = mutableListOf(
    "FerghanaHorse Spade K", "VioletStallion Diamond K", "RedHare Heart 5", "Shadowrunner Spade 5"
)

//Four Suits: Heart(����), Spade�����ң�, Diamond����Ƭ��, Club��÷����
val commandCardList: MutableList<String> = mutableListOf(
    "Acedia Club 6",
    "Acedia Spade 6",
    "Barbarian Spade 7",
    "Barbarian Club 7",
    "Barbarian Spade 6",
    "Duel Spade A",
    "Duel Club A",
    "Duel Diamond A",
    "Duel Club A",
    "RainArrows Heart A",
    "StealingSheep Spade J",
    "StealingSheep Spade 3",
    "StealingSheep Spade 4",
    "StealingSheep Diamond 3",
    "StealingSheep Diamond 4",
    "Dismantle Heart Q",
    "Dismantle Heart 3",
    "Dismantle Spade 3",
    "Dismantle Spade 4",
    "Dismantle Club 3",
    "Dismantle Club 4",
    "Dismantle Club Q",
    "Dismantle Club K",
    "Negate Spade J",
    "Negate Club Q",
    "Negate Club K",


    )

abstract class CardFactory {
    abstract fun createCard(): Card

}

class BasicCardFactory : CardFactory() {
    override fun createCard(): Card {
        var list: MutableList<Card> = mutableListOf(Attack(null, null), Dodge(null, null), Peach(null, null))

        val card: Card = list[(0 until list.size).random()]
        val suits: MutableList<String> = mutableListOf("Heart", "Spade", "Diamond", "Club")
        val ranks: MutableList<String> = mutableListOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

        if (card is Attack) {
            card.suit = suits[(0 until suits.size).random()]
            if (card.suit == "Heart") {
                card.rank = ranks[(9..10).random()]
            } else if (card.suit == "Spade") {
                card.rank = ranks[(6..9).random()]
            } else if (card.suit == "Diamond") {
                card.rank = ranks[(5..9).random()]
            } else if (card.suit == "Club") {
                card.rank = ranks[(1..10).random()]
            }
        } else if (card is Dodge) {
            card.suit = suits[intArrayOf(0, 2).random()]
            if (card.suit == "Heart") {
                card.rank = ranks[intArrayOf(1, 12).random()]
            } else if (card.suit == "Diamond") {
                card.rank = ranks[(1..10).random()]
            }
        } else if (card is Peach) {
            card.suit = suits[intArrayOf(0, 2).random()]
            if (card.suit == "Heart") {
                card.rank = ranks[intArrayOf(2, 3, 5, 6, 7, 8, 11).random()]
            } else if (card.suit == "Diamond") {
                card.rank = ranks[11]
            }
        }

        return card
    }
}

class WeaponFactory : CardFactory() {
    var weaponCardList: MutableList<String> = mutableListOf(
        "Blue Steel Blade,Club,6",
        "Zhuge Crossbow,Spade,A",
        "Zhuge Crossbow,Diamond,A",
        "Yin-Yang Swords,Club,2",
        "Yin-Yang Swords,Heart,K",
        "Green Dragon Blade,Club,5",
        "Serpent Spear,Club,Q",
        "Rock Cleaving Axe,Diamond,5",
        "Kirin Bow,Heart,5",
    )

    override fun createCard(): Card {
        var random = weaponCardList.random()
        weaponCardList.remove(random)
        var information = random.split(",")
        val type = information[0]
        val suit = information[1]
        val rank = information[2]
        var card = when (type) {
            "Zhuge Crossbow" -> Zhuge_Crossbow(null, null)
            "Yin-Yang Swords" -> Yin_Yang_Swords(null, null)
            "Green Dragon Blade" -> Green_Dragon_Blade(null, null)
            "Serpent Spear" -> Serpent_Spear(null, null)
            "Rock Cleaving Axe" -> Rock_Cleaving_Axe(null, null)
            "Kirin Bow" -> Kirin_Bow(null, null)
            "Blue Steel Blade" -> Blue_Steel_Blade(null, null)
            else -> throw IllegalArgumentException("Invalid Weapon Card")
        }
        card.suit = suit
        card.rank = rank
        weaponCardList.remove(random)
        return card
    }
}

class DefenseFactory : CardFactory() {
    var defenseCardList: MutableList<String> = mutableListOf(
        "Eight Trigrams Formation,Spade,2", "Eight Trigrams Formation,Club,2",
    )

    override fun createCard(): Card {
        var random = defenseCardList.random()
        var information = random.split(',')
        val type = information[0]
        val suit = information[1]
        val rank = information[2]
        var card = when (type) {
            "Eight Trigrams Formation" -> Eight_Trigrams_Formation(null, null)
            else -> throw IllegalArgumentException("Invalid Weapon Card")
        }
        card.suit = suit
        card.rank = rank
        defenseCardList.remove(random)
        return card
    }
}

class HorseCardFactory() : CardFactory() {
    override fun createCard(): Card {
        if (HorseCardList.size == 0) throw IllegalArgumentException("All Lords has been created")
        val tar: String = HorseCardList.random()

        var type = tar.split(" ")

        val gen = when (tar) {
            "FerghanaHorse Spade K" -> Ferghana(null, null)
            "VioletStallion Diamond K" -> VioletStallion(null, null)
            "RedHare Heart 5" -> RedHare(null, null)
            "Shadowrunner Spade 5" -> Shadowrunner(null, null)
            else -> throw IllegalArgumentException("Invalid General")
        }

        gen.rank = type[2]
        gen.suit = type[1]

        return gen

    }
}


class CommandCardFactory : CardFactory() {

    override fun createCard(): Card {
        if (commandCardList.size == 0) throw IllegalArgumentException("All Lords has been created")
        val cardInfo = commandCardList.random()
        val cardInfoList = cardInfo.split(" ")
        var tar = cardInfoList[0]
        val card = when (tar) {
            "Acedia" -> Acedia(null, null)
            "Barbarian" -> Barbarians(null, null)
            "Duel" -> Duel(null, null)
            "RainArrows" -> RainArrows(null, null)
            "StealingSheep" -> StealingSheep(null, null)
            "Dismantle" -> Dismantle(null, null)
            "Negate" -> Negate(null, null)
            else -> throw IllegalArgumentException("Invalid Card")
        }
        card.suit = cardInfoList[1]
        card.rank = cardInfoList[2]



        return card

    }


}
