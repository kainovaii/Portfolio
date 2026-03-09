package fr.kainovaii.porfolio.app.domain.message;

import fr.kainovaii.obsidian.di.annotations.Repository;
import org.javalite.activejdbc.LazyList;

import java.util.List;

@Repository
public class MessageRepository
{
    public LazyList<Message> findAll() {
        return Message.findAll();
    }

    public Message findById(int id) {
        return Message.findById(id);
    }

    public List<Message> findAllSortedByDate() {
        return Message.findAll().orderBy("created_at DESC");
    }

    public boolean delete(int id)
    {
        Message message = findById(id);
        return message.delete();
    }

    public Boolean create(String name, String email, String content)
    {
        Message message = new Message();
        message.setName(name);
        message.setEmail(email);
        message.setMessage(content);
        return message.saveIt();
    }

    public Boolean update(int id, String name, String email, String content)
    {
        Message message = findById(id);
        message.setName(name);
        message.setEmail(email);
        message.setMessage(content);
        return message.saveIt();
    }
}
