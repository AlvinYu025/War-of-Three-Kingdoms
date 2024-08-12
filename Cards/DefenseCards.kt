package Cards

import Generals.General

class Eight_Trigrams_Formation(initializer: General?, receiver: General?) : DefenseCard(initializer, receiver){
    override val name = "Eight Trigrams Formation"
    override fun execute(initializer: General, receiver: General?): Boolean {
        if(valid == false){
            println("Defense card is banned.")
            return false
        }
        val random = (0..1).random()
        if(random <= 0.5){
            println("[Eight Trigrams Formation] of ${initializer.name}: Success!")
            return true
        }
        else{
            println("[Eight Trigrams Formation] of ${initializer.name}: Failure.")
            return false
        }
    }
}