package core.ecs;

public abstract class Component {
    public abstract void init();
    public abstract void update(float dt);
    public abstract void dispose();
}
