package cs2063.groceryshopper.model

data class Item(val id: Int, val tripId : Int, val price: Double, val itemName: String){
    override fun toString(): String {
        return "Item: {id: $id, tripId: $tripId, price: $price, itemName: $itemName}"
    }
}
