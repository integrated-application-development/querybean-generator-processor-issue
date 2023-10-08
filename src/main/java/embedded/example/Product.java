package embedded.example;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Product {
  @Id
  public int id;

  @OneToMany(mappedBy = "product")
  public List<OrderItem> orderItems;

  public Product(int id) {
    this.id = id;
  }
}
