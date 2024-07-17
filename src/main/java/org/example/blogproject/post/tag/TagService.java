package org.example.blogproject.post.tag;

import lombok.RequiredArgsConstructor;
import org.example.blogproject.post.tag.Tag;
import org.example.blogproject.post.tag.TagRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName).orElseGet(() -> {
            Tag newTag = new Tag();
            newTag.setName(tagName);
            return tagRepository.save(newTag);
        });
    }
}
