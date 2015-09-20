test = {
  'names': [
    'q7B',
    '7B',
    'qB7',
    'B7'
  ],
  'points': 3,
  'suites': [
    [
      {
        'test': r"""
        >>> # Testing HungryAnt parameters
        >>> hungry = HungryAnt()
        >>> HungryAnt.food_cost
        4
        >>> hungry.armor
        1
        """,
        'type': 'doctest'
      },
      {
        'test': r"""
        >>> # Testing HungryAnt eats and digests
        >>> hungry = HungryAnt()
        >>> super_bee, wimpy_bee = Bee(1000), Bee(1)
        >>> place = colony.places["tunnel_0_0"]
        >>> place.add_insect(hungry)
        >>> place.add_insect(super_bee)
        >>> hungry.action(colony)   # super_bee is no match for HungryAnt!
        >>> super_bee.armor
        0
        >>> place.add_insect(wimpy_bee)
        >>> for _ in range(3):
        ...     hungry.action(colony)  # digesting...not eating
        >>> wimpy_bee.armor
        1
        >>> hungry.action(colony)    # back to eating!
        >>> wimpy_bee.armor
        0
        """,
        'type': 'doctest'
      }
    ],
    [
      {
        'test': r"""
        >>> # Testing HungryAnt only waits when digesting
        >>> hungry = HungryAnt()
        >>> place = colony.places["tunnel_0_0"]
        >>> place.add_insect(hungry)
        >>> # Wait a few turns before adding Bee
        >>> for _ in range(5):
        ...     hungry.action(colony)  # shouldn't be digesting
        >>> bee = Bee(3)
        >>> place.add_insect(bee)
        >>> hungry.action(colony)  # Eating time!
        >>> bee.armor
        0
        """,
        'type': 'doctest'
      }
    ],
    [
      {
        'test': r"""
        >>> # Testing HungryAnt digest time looked up on instance
        >>> very_hungry = HungryAnt()  # Add very hungry caterpi- um, ant
        >>> very_hungry.time_to_digest = 0
        >>> place = colony.places["tunnel_0_0"]
        >>> place.add_insect(very_hungry)
        >>> for _ in range(100):
        ...     place.add_insect(ants.Bee(3))
        >>> for _ in range(100):
        ...     very_hungry.action(colony)   # Eat all the bees!
        >>> len(place.bees)
        0
        """,
        'type': 'doctest'
      }
    ]
  ]
}