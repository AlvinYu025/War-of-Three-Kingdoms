interface State {
    fun playNextCard(){}
}

class HealthyState(val strategy:LiuBeiStrategy): State{
    override fun playNextCard() {
        if(strategy.general.currentHP<2){
            strategy.state = UnhealthyState(strategy)
            strategy.state.playNextCard()
        }
        else{
            println("${strategy.general.name} is healthy.")
        }
    }
}

class UnhealthyState(val strategy:LiuBeiStrategy): State{
    override fun playNextCard() {
        if(strategy.general.currentHP>=2){
            strategy.state = HealthyState(strategy)
            println("${strategy.general.name} is now healthy.")
        }
        else {
            if(strategy.general.hand.size >= 2){
                println("${strategy.general.name} is not healthy")
                val beforeHand = strategy.general.hand.size
                val beforeHP = strategy.general.currentHP
                strategy.general.discard(2)
                strategy.general.currentHP++
                val afterHand = strategy.general.hand.size
                val afterHP = strategy.general.currentHP
                println("[Benevolence] ${strategy.general.name} gives away 2 cards (from ${beforeHand} to ${afterHand}) and recover 1 HP (from ${beforeHP} to ${afterHP}).")
            }
            else{
                println("[Benevolence] ${strategy.general.name} fails to recover 1 HP due to insufficient cards.")
            }
            if(strategy.general.currentHP>=2){
                strategy.state = HealthyState(strategy)
                println("${strategy.general.name} leaves unhealthy state.")
                strategy.state.playNextCard()
            }
        }
    }
}