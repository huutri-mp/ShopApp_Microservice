package com.example.productservice.util;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntityWithSlug {

    @Column(unique = true)
    protected String slug;

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (slug == null || slug.isEmpty()) {
            slug = SlugUtil.toSlug(getSlugSource());
        }
    }

    protected abstract String getSlugSource();
}
