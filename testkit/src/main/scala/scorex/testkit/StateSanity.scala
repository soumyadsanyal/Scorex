package scorex.testkit

import org.scalacheck.Gen
import org.scalatest.prop.{GeneratorDrivenPropertyChecks, PropertyChecks}
import org.scalatest.{Matchers, PropSpec}
import scorex.core.PersistentNodeViewModifier
import scorex.core.consensus.SyncInfo
import scorex.core.transaction.Transaction
import scorex.core.transaction.box.Box
import scorex.core.transaction.box.proposition.Proposition
import scorex.core.transaction.state.{MinimalState, StateChanges}

import scala.util.Random

class StateSanity[P <: Proposition,
TX <: Transaction[P],
PM <: PersistentNodeViewModifier[P, TX],
SI <: SyncInfo,
B <: Box[P]] extends PropSpec with GeneratorDrivenPropertyChecks with Matchers with PropertyChecks {
  type ST = MinimalState[P, B, TX, PM, _ <: MinimalState[P, B, TX, PM, _]]

  def appendedBoxesAreInState(state: ST, stateChangesGen: Gen[StateChanges[P, B]]): Unit = {
    forAll(stateChangesGen) { c =>
      val newState = state.applyChanges(c, Array.fill(32)(Random.nextInt(Byte.MaxValue).toByte)).get
      c.toAppend.foreach { b =>
        newState.closedBox(b.id).isDefined shouldBe true
      }
    }
  }

}
