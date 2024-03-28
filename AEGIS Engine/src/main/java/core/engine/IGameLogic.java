package core.engine;

public interface IGameLogic {
    void init();
    void update(float dt);
    void render(float dt);
    void updateInputs(float dt);
    void dispose();
}
