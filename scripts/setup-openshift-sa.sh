#!/bin/bash
#
# OpenShift Service Account Setup Script
# Purpose: Create a GitHub Actions service account with permission to deploy to OpenShift
#
# USAGE:
#   1. Connect to your OpenShift cluster:
#      oc login --token=<your-token> --server=<api-server-url>
#   2. Run this script:
#      bash scripts/setup-openshift-sa.sh
#   3. Copy the generated token and add it to GitHub as a secret named OPENSHIFT_TOKEN
#      GitHub Settings → Secrets and variables → Actions → New repository secret

set -e

NAMESPACE="brohjoe1-dev"
SA_NAME="github-actions-sa"

echo "=========================================="
echo "OpenShift GitHub Actions Setup"
echo "=========================================="
echo ""

# Check if oc is installed
if ! command -v oc &> /dev/null; then
    echo "ERROR: 'oc' CLI not found. Please install the OpenShift CLI."
    echo "Download: https://mirror.openshift.com/pub/openshift-v4/clients/ocp/"
    exit 1
fi

# Check if logged in
echo "[1/4] Checking OpenShift login..."
if ! oc whoami &> /dev/null; then
    echo "ERROR: Not authenticated to OpenShift. Please run: oc login"
    exit 1
fi
CURRENT_USER=$(oc whoami)
echo "✓ Logged in as: $CURRENT_USER"
echo ""

# Set namespace
echo "[2/4] Switching to namespace: $NAMESPACE"
oc project "$NAMESPACE" || {
    echo "ERROR: Could not switch to namespace '$NAMESPACE'."
    echo "Make sure the namespace exists and you have access."
    exit 1
}
echo "✓ Using namespace: $NAMESPACE"
echo ""

# Create service account
echo "[3/4] Creating service account: $SA_NAME"
if oc get serviceaccount "$SA_NAME" -n "$NAMESPACE" &> /dev/null; then
    echo "⚠ Service account already exists. Skipping creation."
else
    oc create serviceaccount "$SA_NAME" -n "$NAMESPACE"
    echo "✓ Service account created: $SA_NAME"
fi
echo ""

# Grant permissions
echo "[4/4] Granting edit role to service account..."
oc policy add-role-to-user edit -z "$SA_NAME" -n "$NAMESPACE" || {
    echo "⚠ Role binding may already exist. Continuing..."
}
echo "✓ Service account has edit permissions"
echo ""

# Generate token
echo "=========================================="
echo "Generating authentication token..."
echo "=========================================="
echo ""

TOKEN=$(oc create token "$SA_NAME" -n "$NAMESPACE" --duration=8760h)

echo "✓ Token generated (valid for 1 year)"
echo ""
echo "IMPORTANT: Copy the token below and add it to GitHub:"
echo "---"
echo "$TOKEN"
echo "---"
echo ""
echo "GitHub Setup Steps:"
echo "  1. Go to: https://github.com/brohjoe1/personnel-tracker/settings/secrets/actions"
echo "  2. Click 'New repository secret'"
echo "  3. Name: OPENSHIFT_TOKEN"
echo "  4. Value: [paste the token from above]"
echo "  5. Click 'Add secret'"
echo ""
echo "Verify the deployment can now access OpenShift:"
echo "  1. Push a commit to the main branch"
echo "  2. Check GitHub Actions workflow at: https://github.com/brohjoe1/personnel-tracker/actions"
echo ""
