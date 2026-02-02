# Neural AI Overview

This package provides the scaffolding for a deep learning player controller that can plug into Forge's
existing AI hooks. It defines four core areas:

## 1) State representation
`NeuralStateEncoder` converts the live `Game` and `Player` perspective into a compact float vector.
`SimpleStateEncoder` demonstrates a dense, normalized representation that includes:

- Phase one-hot encoding.
- Turn number (normalized).
- Perspective player + opponents: life, poison, hand size, library size, graveyard size, exile size,
  battlefield size, lands in play, creatures in play, and floating mana.

The encoder returns `NeuralState`, which includes the feature vector and corresponding feature names
for debugging/inspection.

## 2) Policy and action/choice representation
`NeuralAction` and `NeuralActionType` describe *what* the policy can choose from. The system treats
spell casts, target selections, modal choices, and other discrete decisions as a shared action
abstraction. `NeuralActionSpace` is the ordered list of available actions. The neural model outputs
policy logits aligned to this action order.

`NeuralActionSpaceBuilder` provides helpers to build action spaces from spell ability lists, target
lists, or explicit numeric choices. The intention is to extend this builder with additional helpers
as more decision points are routed through the neural controller.

## 3) Value output
`NeuralPolicyOutput` carries two outputs:
- `logits`: the policy vector (aligned with the action space).
- `value`: a scalar evaluation of the current position from the perspective player.

This is the standard "policy + value" output needed for modern self-play training loops.

## 4) Training data generation and training system
The training pipeline is composed of:
- `NeuralTrainingSample`: (state, policy target, value target).
- `NeuralReplayBuffer`: fixed-size experience buffer for sampling batches.
- `NeuralTrainingPipeline`: runs self-play generation and model updates.
- `NeuralSelfPlayGenerator`: interface to plug in game simulation that returns training samples.

To build a full trainer, implement a `NeuralSelfPlayGenerator` that plays games using
`PlayerControllerAiNeural`, converts each decision into a policy target (e.g., MCTS visit counts or
behavioral cloning probabilities), and assigns value targets (e.g., terminal results or discounted
returns). The pipeline will manage replay buffer and batch updates.
