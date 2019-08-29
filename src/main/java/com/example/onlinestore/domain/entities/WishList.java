package com.example.onlinestore.domain.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "wishlists")
    public class WishList extends BaseEntity {

    private Set<Product> products;
    private User customer;

    public WishList() {
    }

    @ManyToMany(targetEntity = Product.class)
    @JoinTable(
            name = "wishlists_products",
            joinColumns = @JoinColumn(name = "wishlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id")
    )
    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }
}
