package com.example.echoling.data.Collection;


import com.example.echoling.data.Translation.Translation;
import com.example.echoling.data.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    public Collection createCollection(String name, User user) {
        Collection collection = new Collection(name, user);
        return collectionRepository.save(collection);
    }
    public void saveCollection(Collection collection) {
        collectionRepository.save(collection);
    }

    public void deleteCollection(Collection collection) {
        collectionRepository.delete(collection);
    }

    public List<Collection> getCollectionsByUser(User user) {
        return collectionRepository.findByUser(user);
    }

    public void addTranslationToCollection(Collection collection, Translation translation) {
        collection.getTranslations().add(translation);
        collectionRepository.save(collection);
    }

    public void removeTranslationFromCollection(Collection collection, Translation translation) {
        collection.getTranslations().remove(translation);
        collectionRepository.save(collection);
    }

    public Collection findById(Long id) {
        return collectionRepository.findById(id).orElse(null);
    }
}