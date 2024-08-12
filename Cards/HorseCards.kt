package Cards

import Generals.General


class Ferghana(general: General?, general1: General?) : HorseCard(general,general1){
    override val name: String = "Ferghana Horse";
    override fun run() {
    }

}

class Shadowrunner(general: General?, general1: General?): HorseCard(general, general1){
    override val name: String = "Shadowrunner";
    override fun run() {

    }
}

class RedHare(general: General?, general1: General?): HorseCard(general, general1){
    override val name: String = "Red Hare";
    override fun run() {

    }
}

class VioletStallion(general: General?, general1: General?): HorseCard(general, general1){
    override val name: String = "Violet Stallion";
    override fun run() {

    }
}
