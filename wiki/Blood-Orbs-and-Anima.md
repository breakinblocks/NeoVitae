# Blood Orbs and Anima

Beneath the surface of the world lies your **Anima**, an invisible lattice of living energy. This is the network that binds your soul to every sigil you wield, every ritual you ignite, every vitaemantic instrument you have ever touched. Think of it as a vast, hidden reservoir of **Essentia Vitae** (EV) refined and stored beyond the physical realm, unique to you alone.

## Binding

When you first grip a bindable item and will it to life, it imprints upon your Anima. From that moment forward, any EV cost the item demands is drawn from your network. Should the Anima run dry, some items will take payment directly from your flesh instead.

Other workings, such as a hungry **[Ritual](Rituals)** that has drained you completely, inflict unceasing nausea until the ritual is silenced or your Anima is replenished.

## Filling the Anima

To fill your Anima, you require an **Orb of Vitae**, a crystallised anchor between your soul and the altar's power.

- **Right-click with the orb in hand** to open your essentia vitae. Near an Ara Vitae the blood flows straight into the altar's basin; away from one it is stored in the orb's own internal reservoir to carry and deposit later. While a **Blessed Sacrifice** from an Incense Altar is upon you, this offering is far greater, after which you fall into Soul Fray.
- **Place the orb within a charged [Ara Vitae](Ara-Vitae-and-Runes).** The basin drinks the orb's stored blood and pours it into your Anima, limited only by your altar's Speed Runes.

The orb harbours that internal reservoir between sacrifices. When you place an orb containing stored blood back upon an Ara Vitae, the altar drains the reservoir at **10x the normal fill rate** and rapidly refills the basin; if the basin is already nearly full, the EV is channeled into your Anima instead. Once the orb is empty, normal behavior resumes. To automate this, seat the orb in an **[Orb Vitae Link](Routing-Network)** beside an altar and let it charge your Anima continuously.

### Harvest of the Slain

Slay any creature while an Orb of Vitae rests in your off-hand and the orb drinks deeply of the fallen. The victim's life force is converted into EV and drawn directly into the orb's internal reservoir at **10 EV per point of maximum health**:

- A slain zombie (20 HP) yields **200 EV**.
- An Ender Dragon (200 HP) yields **2,000 EV**.

The **Bonus Sacrifice** attribute further multiplies this harvest. Combine the orb's altar-drain behaviour with combat farming; fill the orb on the battlefield, then drop it on the Ara Vitae for a 10x rate top-up.

## Orb Tiers

Each orb is forged at an Ara Vitae of its tier, and each raises your Anima's maximum capacity further than the last.

| Tier | Orb            | Anima Capacity |
|------|----------------|----------------|
| 0    | Novicius Orb   | 5,000 EV       |
| 1    | Discipulus Orb | 25,000 EV      |
| 2    | Veneficus Orb  | 150,000 EV     |
| 3    | Magus Orb      | 1,000,000 EV   |
| 4    | Dominus Orb    | 5,000,000 EV   |
| 5    | Divinus Orb    | 10,000,000 EV  |

Should even the Divinus Orb not sate your ambitions, **Runes of the Orb** carved around the altar stretch the Anima further still. See **[Ara Vitae and Runes](Ara-Vitae-and-Runes)** for the rune families.

## Sanguine Ward

Any Orb of Vitae held in the off-hand may be raised as a ward. Hold the **use key** to conjure a translucent barrier of crystallised Essentia Vitae before you. The ward persists for as long as you hold the key and vanishes the instant you release it.

While active, the ward blocks all damage originating from your **front arc**, draining **50 EV per second** from your Anima to maintain it. If your Anima falls below **200 EV**, the ward cannot be raised. The exact drain rate and threshold are configurable in `config/neovitae-server.toml`, so pack-makers may tune the cost to taste.

> The ward moves with you, always positioned directly ahead. Attacks from behind or the sides bypass it entirely. Step carefully when surrounded.

## Reading the Anima

A comparator placed against an Ara Vitae with a **Blood Stained Glass** block beneath it reads the Anima of whichever player's orb sits in the basin. Signal strength scales to the orb's tier, not the network's absolute size; the same 500,000 EV will overflow a Novicius reading but barely tickle a Magus. When the orb is bound to a team via NeoVitae Teams, the comparator reads the team's pooled Anima automatically. See the [Ara Vitae](Ara-Vitae-and-Runes) page for full redstone integration.

See also: **[Sigils](Sigils)** (which draw on the Anima), **[Sentient Armor](Sentient-Armor)** (the Repair upgrade pays in EV), **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** (a parallel network for a different kind of will).
