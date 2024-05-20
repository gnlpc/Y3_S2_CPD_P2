DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    score INTEGER NOT NULL DEFAULT 0,
    is_connected BOOLEAN NOT NULL DEFAULT FALSE,
    token VARCHAR(255)
);

CREATE TABLE games (
    id INTEGER PRIMARY KEY,
    user1_id INTEGER NOT NULL,
    user2_id INTEGER NOT NULL,
    game_type VARCHAR(255) CHECK(game_type in ('simple', 'ranked')) NOT NULL DEFAULT 'simple',
    winner_id INTEGER,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id),
    FOREIGN KEY (winner_id) REFERENCES users(id)
);

-- Update the token of the users in the game
DROP TRIGGER IF EXISTS update_token_on_game_creation;

CREATE TRIGGER update_token_on_game_creation
AFTER INSERT ON games
BEGIN
    UPDATE users
    SET token = NEW.id
    WHERE id = NEW.user1_id OR id = NEW.user2_id;
END;

-- Update the score of the user in the ranked type
DROP TRIGGER IF EXISTS update_score_on_game_finish_ranked;

CREATE TRIGGER update_score_on_game_finish_ranked
AFTER UPDATE OF winner_id ON games
WHEN NEW.winner_id IS NOT NULL AND NEW.game_type = 'ranked'
BEGIN
    -- Points to add to the winner
    UPDATE users
    SET score = score + 1
    WHERE id = NEW.winner_id;

    -- Points to remove from the loser
    UPDATE users
    SET score = CASE
                    WHEN score > 0 THEN score - 1
                    ELSE score
                END
    WHERE id IN (NEW.user1_id, NEW.user2_id) AND id != NEW.winner_id;

    -- Set the token to NULL for both players
    UPDATE users
    SET token = NULL
    WHERE id IN (NEW.user1_id, NEW.user2_id);
END;

DROP TRIGGER IF EXISTS update_token_on_game_finish_simple;

-- Create a trigger that updates the token on game finish for simple games
CREATE TRIGGER update_token_on_game_finish_simple
AFTER UPDATE OF winner_id ON games
WHEN NEW.winner_id IS NOT NULL AND NEW.game_type = 'simple'
BEGIN
    UPDATE users
    SET token = NULL
    WHERE id IN (NEW.user1_id, NEW.user2_id);
END;

INSERT INTO users (username, password, score) VALUES
('user1', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user2', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user3', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user4', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user5', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user6', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user7', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user8', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user9', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user10', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user11', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user12', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user13', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user14', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user15', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user16', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user17', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user18', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user19', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user20', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user21', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user22', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user23', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user24', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user25', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user26', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user27', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user28', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user29', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41)),
('user30', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', ABS(RANDOM() % 41));



-- INSERT INTO games (user1_id, user2_id, game_type) VALUES ((SELECT id FROM users WHERE username = 'user1' LIMIT 1), (SELECT id FROM users WHERE username = 'user2' LIMIT 1), 'ranked');

SELECT * FROM games;

-- UPDATE games SET winner_id = 2
-- WHERE id = 1;

-- Uncomment the following line to check the users table
-- SELECT * FROM users;
