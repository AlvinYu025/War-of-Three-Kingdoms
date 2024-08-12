package Factory

import Generals.*
import LiuBeiStrategy
import Lord
import Loyalist
import LoyalistStrategy

import Player
import Rebel
import RebelStrategy
import Spy
import SunQuanStrategy
import javax.naming.ServiceUnavailableException

abstract class GeneralFactory() {

    abstract fun createRandomGeneral(num: Int): General
    abstract fun createPlayer(num: Int): Player

}


val generalList: MutableList<String> = mutableListOf(
    "Zhen Ji",
    "Xu Chu",
    "Zhang Liao",
    "Lv Bu",
    "Diao Chan",
    "Zhao Yun",
    "Zhang Fei",
    "Sun Shang Xiang",
    "Lv Meng",
    "Hua tuo",
    "Huang Gai",
    "Hua Xiong"
)
val lords: MutableList<String> = mutableListOf(
    "Liu Bei",
    "Cao Cao",
    "Sun Quan"
)

class LordFactory : GeneralFactory() {


    override fun createRandomGeneral(num: Int): General {
        if (lords.size == 0) throw IllegalArgumentException("All Lords has been created")
        var tar = lords.random()
        val gen = when (tar) {
            "Liu Bei" -> LiuBei(Lord())
            "Cao Cao" -> CaoCao(Lord())
            "Sun Quan" -> SunQuan(Lord())
            else -> throw IllegalArgumentException("Invalid General")
        }

        println("General $tar created")
        lords.remove(tar)
        gen.currentHP = gen.maxHP
        if (tar == "Liu Bei") {
            val str = LiuBeiStrategy(gen)
            gen.strategy = str
            str.initializeStrategy()
        } else if (gen is SunQuan) {
            gen.initializeIdentityStrategy()
        } else {
            gen.strategy = LoyalistStrategy(gen)
        }
        println("${gen.name}, a ${gen.identity}, has ${gen.currentHP} health point")


        return gen

    }


    override fun createPlayer(num: Int): Player {

        return Lord()
    }
}

open class NonLordFactory(general: General, val lord: Lord) : GeneralFactory() {
    var cao: WeiGeneral? = null
    var liu: ShuGeneral? = null

    init {
        if (general is WeiGeneral) {
            this.cao = general
        } else if (general is LiuBei) {
            this.liu = general
        }


    }

    override fun createRandomGeneral(num: Int): General {

        var tar = generalList.random()
        val player = createPlayer(num)
        val gen = when (tar) {
            "Liu Bei" -> LiuBei(player)
            "Cao Cao" -> CaoCao(player)
            "Sun Quan" -> SunQuan(player)
            "Zhen Ji" -> ZhenJi(player)
            "Xu Chu" -> XuChu(player)
            "Zhang Liao" -> ZhangLiao(player)
            "Lv Bu" -> LvBu(player)
            "Diao Chan" -> DiaoChan(player)
            "Zhao Yun" -> ZhaoYun(player)
            "Zhang Fei" -> ZhangFei(player)
            "Sun Shang Xiang" -> SunShangXiang(player)
            "Lv Meng" -> LvMeng(player)
            "Hua tuo" -> Huatuo(player)
            "Huang Gai" -> HuangGai(player)
            "Hua Xiong" -> HuaXiong(player)

            else -> throw IllegalArgumentException("Invalid General")
        }

        if (gen is WeiGeneral && cao is WeiGeneral) {
            if (gen.player is Loyalist) {
                gen.headGeneral = cao
                cao?.setNext(gen)
                println("${gen.name} added to the Wei chain.")
            } else if (gen.player is Spy && !gen.player.isRevealed) {
                gen.headGeneral = cao
                cao?.setNext(gen)
                println("${gen.name} added to the Wei chain.")
            }

        } else if (gen is ShuGeneral && liu is ShuGeneral) {
            (liu as LiuBei).addShu(gen)
            println("${gen.name} added to the Shu chain.")
        }

        if (gen is NeutralGeneral) {
            if (countNe == 0) {
                current = gen
                headNe = gen
            } else {
                current!!.nextNe = gen
            }
            println("${gen.name} added to the NeuralGeneral chain.")
            countNe++
        }

        if (player is Spy) {
            lord.attach(gen.player as Spy)

            if (!gen.player.isRevealed) {
                gen.strategy = LoyalistStrategy(gen)
            } else {
                gen.strategy = RebelStrategy(gen)
            }
        }


        if (player is Loyalist) {

            gen.strategy = LoyalistStrategy(gen)
        }
        if (player is Rebel) {
            gen.strategy = RebelStrategy(gen)
        }
        if (gen is SunShangXiang) {
            gen.initializeIdentityStrategy()
        }
        if (gen is HuangGai) {
            gen.initializeIdentityStrategy()
        }
        if (gen is LvMeng) {
            gen.initializeIdentityStrategy()
        }
        if (gen is Huatuo) {
            gen.initializeIdentityStrategy()
        }
        if (gen is SunQuan) {
            gen.initializeIdentityStrategy()
        }
        if (gen is DiaoChan) {
            gen.initializeIdentityStrategy()
        }



        generalList.remove(tar)
//        gen.numOfCards = 4
        gen.currentHP = gen.maxHP
        println("General $tar created")
        println("${gen.name}, a ${gen.identity}, has ${gen.currentHP} health point")
        return gen

    }

    override fun createPlayer(num: Int): Player {
        if (generalList.size == 0) throw IllegalArgumentException("All generals has been created")
        val player = when (num) {
            2 -> Rebel()
            3, 10 -> Spy()
            4 -> Loyalist()
            5 -> Rebel()
            6 -> Rebel()
            7 -> Loyalist()
            8 -> Rebel()
            9 -> Loyalist()


            else -> throw IllegalArgumentException("Player can not be added, check number of players")
        }

        return player
    }
}


