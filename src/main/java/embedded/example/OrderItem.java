package embedded.example;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OrderItem {
  @Embeddable
  public static final class Key implements Serializable {
    @Column
    public int orderId;
    @Column
    public int productId;
  }

  @EmbeddedId
  public Key pk;

  @ManyToOne(optional = false)
  @JoinColumn(
      name = "orderId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false
  )
  public Order order;

  @ManyToOne(optional = false)
  @JoinColumn(
      name = "productId",
      referencedColumnName = "id",
      insertable = false,
      updatable = false
  )
  public Product product;

  public OrderItem(Key pk, Order order, Product product) {
    this.pk = pk;
    this.order = order;
    this.product = product;
  }
}
