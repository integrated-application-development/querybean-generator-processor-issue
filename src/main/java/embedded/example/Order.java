package embedded.example;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Order {
  @Id
  public int id;

  @OneToMany(mappedBy = "order")
  public List<OrderItem> items;

  public Order(int id) {
    this.id = id;
  }
}
