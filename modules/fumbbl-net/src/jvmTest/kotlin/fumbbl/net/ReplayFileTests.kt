package fumbbl.net

import com.jervisffb.fumbbl.net.FumbblFileReplayAdapter
import com.jervisffb.fumbbl.net.adapter.FumbblReplayAdapter
import com.jervisffb.utils.platformFileSystem
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.Ignore
import kotlin.test.assertTrue

class ReplayFileTests {

    @Test
    @Ignore // Only run manually
    fun readReplayFile() =
        runBlocking {
            val file = platformFileSystem.canonicalize("".toPath()) / ("../../replays/game-1624379.json".toPath())
            val adapter = FumbblFileReplayAdapter(file)
            adapter.start()
            var isDone = false
            while (!isDone) {
                val cmd = adapter.receive()
                isDone = cmd.lastCommand
            }
            adapter.close()
        }

    @Test
    @Ignore // Fouling is broken. We need to revisit How rules are setup. Also, only run manually
    fun convertReplayFileToJervisCommands() =
        runBlocking {
            val fumbbl = FumbblReplayAdapter("../../replays/game-1624379.json".toPath(), checkCommandsWhenLoading = true)
            runBlocking {
                fumbbl.loadCommands()
            }
        }

    @Test
    @Ignore // See above
    fun convertReplayFileToJervisCommands2() =
        runBlocking {
            val fumbbl = FumbblReplayAdapter("../../replays/game-1744037.json".toPath(), checkCommandsWhenLoading = true)
            runBlocking {
                fumbbl.loadCommands()
            }
        }

    // --- Replay loading tests ---

    // Issue 1: Deserialization — FUMBBL replays contain model change types
    // that Jervis doesn't know about yet. This test verifies that a real
    // FUMBBL replay (Human vs Human, upstream-only rosters) can be deserialized
    // without throwing (all model change types are known to Jervis).
    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    fun loadReplayGame1858100Deserializes() =
        runBlocking {
            val file = platformFileSystem.canonicalize("".toPath()) / ("../../replays/game-1858100.json".toPath())
            val adapter = FumbblFileReplayAdapter(file)
            adapter.start()
            // Must consume gameState first — the channel has capacity=1 and
            // the background coroutine blocks on send until we receive it.
            adapter.getGame()
            var commandCount = 0
            var isDone = false
            while (!isDone) {
                val cmd = adapter.receive()
                commandCount++
                isDone = cmd.lastCommand
            }
            adapter.close()
            assertTrue(commandCount > 0, "Should have received commands from replay")
        }

    // Issue 2: Mapper chain — after deserialization succeeds, the mapper chain
    // must be able to process all commands and drive the Jervis engine through
    // the full game. This test verifies that `FumbblReplayAdapter.loadCommands()`
    // completes without hanging or throwing.
    @Test
    @Timeout(value = 120, unit = TimeUnit.SECONDS)
    fun loadReplayGame1858100Processes() =
        runBlocking {
            val fumbbl = FumbblReplayAdapter("../../replays/game-1858100.json".toPath(), checkCommandsWhenLoading = false)
            fumbbl.loadCommands()
            assertTrue(fumbbl.getGame() != null, "Game should be loaded")
            assertTrue(fumbbl.getCommands().isNotEmpty(), "Commands should be generated")
        }

    // Issue 3: Node matching — with checkCommandsWhenLoading=true, the mapper chain
    // verifies that the Jervis engine's procedure tree matches the expected node at
    // each command. This is the strictest test — any mismatch means the mapper's
    // assumed action order doesn't match the engine's actual procedure flow.
    @Test
    @Timeout(value = 120, unit = TimeUnit.SECONDS)
    fun loadReplayGame1858100Checked() =
        runBlocking {
            val fumbbl = FumbblReplayAdapter("../../replays/game-1858100.json".toPath(), checkCommandsWhenLoading = true)
            fumbbl.loadCommands()
            assertTrue(fumbbl.getGame() != null, "Game should be loaded")
            assertTrue(fumbbl.getCommands().isNotEmpty(), "Commands should be generated")
        }
}
