import Cards.Card
import Cards.DefenseCard
import Cards.WeaponCard

interface Player {
    open var currentHP: Int
    open var identity:String

    var hand: MutableList<Card> //Cards in hand. Initial cards in hand are equal to health point.
    var equipment:MutableList<Card>
    var weapon: WeaponCard?
    var defense: DefenseCard?
    fun shouldHelpLord():Boolean
}

interface Observer{

    open fun update(dodged: Boolean)
}

interface Subject{

    open fun notifyObservers(dodged: Boolean)
    open fun attach(observer: Observer)
    open fun dettach(observer: Observer)
}

class Lord() :Player, Subject{
    override var currentHP: Int = 0
    private var observers = mutableListOf<Observer>()

    override var identity: String = "Lord"
    private var state: String = ""
    override var equipment: MutableList<Card> = mutableListOf()
    override var weapon: WeaponCard? = null
    override var defense: DefenseCard? = null
    override var hand: MutableList<Card> = mutableListOf()


    override fun shouldHelpLord(): Boolean {
        return false
    }

    override fun notifyObservers(dodged: Boolean) {
        observers.forEach { it.update(dodged) }
    }

    override fun attach(observer: Observer) {

        observers.add(observer)
    }

    override fun dettach(observer: Observer) {

        observers.remove(observer)
    }


}

class Loyalist :Player{
    override var currentHP: Int = 0

    override var identity: String = "Loyalist"

    override var hand: MutableList<Card> = mutableListOf()
    override var equipment: MutableList<Card> = mutableListOf()
    override var weapon: WeaponCard? = null
    override var defense: DefenseCard? = null
    override fun shouldHelpLord(): Boolean {
        return true
    }
}
class Spy :Player, Observer{
    override var currentHP: Int = 0

    override var identity: String = "Spy"
    var isRevealed = false

    var levelOfRisk = 5

    override var hand: MutableList<Card> = mutableListOf()
    override var equipment: MutableList<Card> = mutableListOf()
    override var weapon: WeaponCard? = null
    override var defense: DefenseCard? = null
    override fun shouldHelpLord(): Boolean {
        if(!isRevealed&&levelOfRisk<2){
            return true
        }
        return false
    }

    override fun update(dodged: Boolean) {
        if(dodged == true){
            println("Current risk level: $levelOfRisk")
        }
        else{
            levelOfRisk-=1
            if(levelOfRisk<0)
                levelOfRisk = 0
            println("Current risk level: $levelOfRisk")
        }

    }
}
class Rebel :Player{
    override var currentHP: Int = 0

    override var identity: String = "Rebel"

    override var hand: MutableList<Card> = mutableListOf()
    override var equipment: MutableList<Card> = mutableListOf()
    override var weapon: WeaponCard? = null
    override var defense: DefenseCard? = null
    override fun shouldHelpLord(): Boolean {
        return false
    }
}

