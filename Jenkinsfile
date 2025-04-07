@Library('jenkins-shared-library') _
pipelineTemplate(
    buildType: 'gradle',
    agentLabel: 'kube-gradle-agent',
    projectKey: 'sboot-order-dispatcher',
    appImage: '361769563347.dkr.ecr.us-east-1.amazonaws.com/sboot-order-dispatcher',
    awsBucket: 'thoggs-sboot-order-dispatcher',
    releaseBranch: 'main',
    awsRegion: 'us-east-1',
    awsRegistry: '361769563347.dkr.ecr.us-east-1.amazonaws.com',
    awsBucketReleaseFileName: 'release-version.txt',
    awsBucketSnapshotFileName: 'snapshot-version.txt'
)