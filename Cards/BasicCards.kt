package Cards

import Generals.General

class Attack(initializer: General?, receiver: General?): BasicCard(initializer, receiver) {
    override val name: String = "Attack"
}

class Dodge(initializer: General?, receiver: General?): BasicCard(initializer, receiver) {
    override val name: String = "Dodge"
}

class Peach(initializer: General?, receiver: General?): BasicCard(initializer, receiver) {
    override val name: String = "Peach"
}